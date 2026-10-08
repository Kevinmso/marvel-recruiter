"""Gera app/src/main/assets/roster_snapshot.json a partir da Comic Vine.

Uso (quando o roster mudar):
    python tools/generate_roster_snapshot.py

Lê a lista curada de app/src/main/java/com/marvel/recruiter/data/remote/CuratedRoster.kt
e a COMIC_VINE_API_KEY de local.properties. Reaproveita entradas já presentes no
snapshot, para não gastar requisições (limite de 200 req/hora por recurso).
"""
import json
import re
import time
import urllib.parse
import urllib.request
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
ROSTER_KT = ROOT / "app/src/main/java/com/marvel/recruiter/data/remote/CuratedRoster.kt"
OUT = ROOT / "app/src/main/assets/roster_snapshot.json"
API = "https://comicvine.gamespot.com/api"
DELAY = 0.6
NL = chr(10)


def api_key() -> str:
    for line in (ROOT / "local.properties").read_text(encoding="utf-8").splitlines():
        if line.startswith("COMIC_VINE_API_KEY="):
            return line.split("=", 1)[1].strip()
    raise SystemExit("COMIC_VINE_API_KEY ausente em local.properties")


KEY = api_key()
UA = {"User-Agent": "MarvelRecruiter/0.1 (trabalho de curso)"}


def source() -> str:
    return ROSTER_KT.read_text(encoding="utf-8")


def list_ids(name: str) -> list[int]:
    src = source()
    start = src.index(name)
    end = src.index(NL + "    )", start)
    body = re.sub(r"//[^" + NL + "]*", "", src[start:end])
    return [int(x) for x in re.findall(r"[0-9]+", body)]


def excluded_ids() -> list[int]:
    m = re.search(r"excludedIssueIds[^" + NL + r"]*setOf\(([^)]*)\)", source())
    if not m:
        return []
    return [int(x) for x in re.findall(r"[0-9]+", m.group(1))]


def get(path: str, **params) -> dict:
    query = urllib.parse.urlencode({"api_key": KEY, "format": "json", **params})
    req = urllib.request.Request(f"{API}/{path}?{query}", headers=UA)
    time.sleep(DELAY)
    with urllib.request.urlopen(req, timeout=30) as resp:
        body = json.load(resp)
    if body.get("status_code") != 1:
        raise SystemExit(f"Comic Vine erro em {path}: {body.get('error')}")
    return body["results"]


def html_to_text(html: str) -> str:
    text = re.sub(r"(?i)<br\s*/?>|</p>", NL, html)
    text = re.sub(r"<[^>]+>", "", text)
    for a, b in (("&nbsp;", " "), ("&amp;", "&"), ("&quot;", '"'), ("&#39;", "'"),
                 ("&ndash;", "\u2013"), ("&mdash;", "\u2014")):
        text = text.replace(a, b)
    lines = [l.strip() for l in text.splitlines() if l.strip()]
    return (NL + NL).join(lines)


def character(cvId: int) -> dict:
    r = get(f"character/4005-{cvId}/", field_list="id,name,real_name,aliases,deck,count_of_issue_appearances,image,powers,teams,character_friends")
    credits = get(f"character/4005-{cvId}/", field_list="id,issue_credits")
    return {
        "cvId": r["id"],
        "name": r["name"],
        "realName": r.get("real_name"),
        "deck": r.get("deck"),
        "aliases": r.get("aliases"),
        "imageUrl": (r.get("image") or {}).get("medium_url"),
        "powerCount": len(r.get("powers") or []),
        "issueAppearances": r.get("count_of_issue_appearances") or 0,
        "teams": [{"cvId": t["id"], "name": t.get("name") or ""} for t in (r.get("teams") or [])],
        "friendCvIds": [f["id"] for f in (r.get("character_friends") or [])],
        "issueIds": sorted({c["id"] for c in (credits.get("issue_credits") or [])}),
    }


def arc(cvId: int) -> dict:
    r = get(f"story_arc/{cvId}/", field_list="id,name,deck,image,description,issues")
    story = html_to_text(r["description"]) if r.get("description") else None
    return {
        "cvId": r["id"],
        "name": r["name"],
        "deck": r.get("deck"),
        "imageUrl": (r.get("image") or {}).get("medium_url"),
        "story": story or None,
        "issueIds": sorted({i["id"] for i in (r.get("issues") or [])}),
    }


def main() -> None:
    char_ids = list_ids("characterCvIds")
    arc_ids = list_ids("arcCvIds")
    excluded = excluded_ids()
    print(f"{len(char_ids)} personagens, {len(arc_ids)} arcos, {len(excluded)} exclusões")

    cached = json.loads(OUT.read_text(encoding="utf-8")) if OUT.exists() else {"characters": [], "arcs": []}
    have_c = {c["cvId"]: c for c in cached["characters"]}
    have_a = {a["cvId"]: a for a in cached["arcs"]}

    characters = []
    for cid in char_ids:
        characters.append(have_c.get(cid) or character(cid))
        print("personagem", cid, "(cache)" if cid in have_c else "")
    arcs = []
    for aid in arc_ids:
        a = have_a.get(aid) or arc(aid)
        a["issueIds"] = [i for i in a["issueIds"] if i not in excluded]
        arcs.append(a)
        print("arco", aid, "(cache)" if aid in have_a else "")

    snapshot = {"excludedIssueIds": excluded, "characters": characters, "arcs": arcs}
    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_text(json.dumps(snapshot, ensure_ascii=False, indent=1), encoding="utf-8")
    print("gravado", OUT)


if __name__ == "__main__":
    main()
