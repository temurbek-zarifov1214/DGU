#!/usr/bin/env python3
# Generates Android vector drawables from the Naqqoshlik heritage design geometry.
import math, os

OUT = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "res", "drawable")
OUT = os.path.abspath(OUT)

def star(cx, cy, points, R, r, phase=None):
    if phase is None: phase = -math.pi / 2
    d = ""
    for i in range(points * 2):
        a = phase + i * math.pi / points
        rad = r if i % 2 else R
        d += ("L" if i else "M") + f"{cx + rad*math.cos(a):.2f} {cy + rad*math.sin(a):.2f} "
    return d + "Z"

def ring(cx, cy, n, R, phase=None):
    if phase is None: phase = -math.pi / 2
    d = ""
    for i in range(n):
        a = phase + i * 2 * math.pi / n
        d += ("L" if i else "M") + f"{cx + R*math.cos(a):.2f} {cy + R*math.sin(a):.2f} "
    return d + "Z"

def circle(cx, cy, r):
    return f"M{cx-r:.2f} {cy:.2f} a{r:.2f} {r:.2f} 0 1 0 {2*r:.2f} 0 a{r:.2f} {r:.2f} 0 1 0 {-2*r:.2f} 0 Z"

def rrect(x, y, w, h, r):
    return (f"M{x+r:.2f} {y:.2f} h{w-2*r:.2f} a{r:.2f} {r:.2f} 0 0 1 {r:.2f} {r:.2f} "
            f"v{h-2*r:.2f} a{r:.2f} {r:.2f} 0 0 1 {-r:.2f} {r:.2f} h{-(w-2*r):.2f} "
            f"a{r:.2f} {r:.2f} 0 0 1 {-r:.2f} {-r:.2f} v{-(h-2*r):.2f} a{r:.2f} {r:.2f} 0 0 1 {r:.2f} {-r:.2f} Z")

def P(d, fill=False, sw=1.5, op=1.0, color="#FFFFFF"):
    a = f'\n    <path android:pathData="{d.strip()}"'
    if fill:
        a += f'\n        android:fillColor="{color}"'
        if op != 1.0: a += f'\n        android:fillAlpha="{op}"'
    else:
        a += (f'\n        android:fillColor="#00000000"'
              f'\n        android:strokeColor="{color}"'
              f'\n        android:strokeWidth="{sw}"'
              f'\n        android:strokeLineCap="round"'
              f'\n        android:strokeLineJoin="round"')
        if op != 1.0: a += f'\n        android:strokeAlpha="{op}"'
    return a + ' />'

def vector(paths, vw=24, vh=24, w=24, h=24, groups=""):
    body = "".join(paths) + groups
    return (f'<?xml version="1.0" encoding="utf-8"?>\n'
            f'<vector xmlns:android="http://schemas.android.com/apk/res/android"\n'
            f'    android:width="{w}dp" android:height="{h}dp"\n'
            f'    android:viewportWidth="{vw}" android:viewportHeight="{vh}">{body}\n</vector>\n')

def write(name, content):
    with open(os.path.join(OUT, name + ".xml"), "w", encoding="utf-8") as f:
        f.write(content)

star8 = star(12, 12, 8, 8.5, 3.7)

# ── Themed naqqoshlik icons (white, tintable) ──────────────────────────────
icons = {
 "tarix": [P("M8 4h9a1 1 0 0 1 1 1v13a2 2 0 0 1-2 2H8"), P("M8 4a2 2 0 0 0-2 2a2 2 0 0 0 2 2h2"),
           P("M16 20a2 2 0 0 0 2-2"), P("M11 9h4"), P("M11 13h4"), P(star(8.5,16,4,1.7,0.7), fill=True)],
 "uslublar": [P(star8), P(ring(12,12,8,4.2), op=0.5)],
 "chizish": [P(circle(12,4.5,1.4)), P("M12 5.8 7 19"), P("M12 5.8 16.4 15"),
             P("M7 19a6.2 6.2 0 0 0 9.4-4"), P("M16.4 15l1.6 3.4")],
 "ilhom": [P("M12 21v-7"), P("M12 14c-3-1-4-5-1.6-7.2C11.4 5.7 12 4 12 4s.6 1.7 1.6 2.8C16 9 15 13 12 14Z"),
           P("M12 17c-2 0-3.4-1.2-3.8-2.4"), P("M12 17c2 0 3.4-1.2 3.8-2.4")],
 "asbob": [P("M5 19l8.5-8.5"), P("M11.5 8.5l2 2-2.2 2.2-2-2z", op=0.9), P("M19 19l-5-5"),
           P("M18.4 14.9 19.9 16.4 18.4 17.9 16.9 16.4z", fill=True, op=0.85)],
 "muallif": [P(rrect(5,6,14,14,2)), P(circle(12,12,2.2)), P("M8.4 18a3.6 3.6 0 0 1 7.2 0"),
             P(star(12,4,4,2,0.9), fill=True)],
 "yoriq": [P("M12 6C9.8 4.9 7.4 4.8 5 5.4V18c2.4-.6 4.8-.5 7 .6"),
           P("M12 6c2.2-1.1 4.6-1.2 7-.6V18c-2.4-.6-4.8-.5-7 .6"), P("M12 6v12.6"),
           P(star(8.3,11.5,4,1.7,0.7), fill=True, op=0.8)],
 "pdf": [P("M14 3H7a1 1 0 0 0-1 1v16a1 1 0 0 0 1 1h10a1 1 0 0 0 1-1V7z"), P("M14 3v4h4"),
         P(star(12,14,8,3.2,1.4), sw=1, op=0.55)],
 "search": [P(circle(10.5,10.5,6)), P("M15 15l4.5 4.5"), P(star(10.5,10.5,4,2.4,1), sw=1, op=0.6)],
 "share": [P(circle(18,5,2.4)), P(circle(6,12,2.4)), P(circle(18,19,2.4)),
           P("M8.1 10.9 15.9 6.1"), P("M8.1 13.1l7.8 4.8")],
 "back": [P("M15 5l-7 7 7 7")],
 "chevron": [P("M9 5l7 7-7 7")],
 "chevdown": [P("M6 9l6 6 6-6")],
 "plus": [P("M12 5v14"), P("M5 12h14"), P(star(12,12,4,2.2,0.9), fill=True)],
 "home": [P(star8, fill=True)],
 "list": [P(rrect(4.5,5.5,15,4,1.4)), P(rrect(4.5,14.5,15,4,1.4))],
 "video": [P(rrect(3.5,6,17,12,3)), P("M10 9.5v5l4.5-2.5z", fill=True)],
 "play": [P("M8 5.5v13l11-6.5z", fill=True)],
 "pause": [P("M9 5.5v13"), P("M15 5.5v13")],
 "download": [P("M12 4v10"), P("M8 11l4 4 4-4"), P("M5 19h14")],
 "check": [P(circle(12,12,8.5)), P("M8.4 12.2l2.4 2.4 4.6-5")],
 "lock": [P(rrect(5.5,11,13,8.5,2)), P("M8.5 11V8a3.5 3.5 0 0 1 7 0v3"), P(circle(12,15,1), fill=True)],
 "fullscreen": [P("M4 9V4h5"), P("M20 9V4h-5"), P("M4 15v5h5"), P("M20 15v5h-5")],
 "bookmark": [P("M7 4h10v16l-5-4-5 4z")],
 "trash": [P("M5 7h14"), P("M10 7V5h4v2"), P("M7 7l1 13h8l1-13"), P("M10.5 11v5"), P("M13.5 11v5")],
 "history": [P("M4 12a8 8 0 1 0 2.4-5.7"), P("M3.5 4.5v3.2h3.2"), P("M12 8v4.4l3 1.8")],
 "grid": [P(rrect(4.5,4.5,6,6,1.4)), P(rrect(13.5,4.5,6,6,1.4)), P(rrect(4.5,13.5,6,6,1.4)), P(rrect(13.5,13.5,6,6,1.4))],
 "sun": [P(circle(12,12,4)), P("M12 3v2"), P("M12 19v2"), P("M3 12h2"), P("M19 12h2"),
         P("M5.6 5.6l1.4 1.4"), P("M17 17l1.4 1.4"), P("M18.4 5.6 17 7"), P("M7 17l-1.4 1.4")],
 "moon": [P("M20 14.5A7.5 7.5 0 1 1 9.5 4a6 6 0 0 0 10.5 10.5Z")],
 "close": [P("M6 6l12 12"), P("M18 6L6 18")],
 "link": [P("M10.5 13.5a3.5 3.5 0 0 0 5 0l3-3a3.5 3.5 0 0 0-5-5l-1.5 1.5"),
          P("M13.5 10.5a3.5 3.5 0 0 0-5 0l-3 3a3.5 3.5 0 0 0 5 5l1.5-1.5")],
 "paste": [P(rrect(6,5,12,16,2)), P(rrect(9,3,6,4,1.2))],
}
for name, paths in icons.items():
    write("nq_" + name, vector(paths))

# ── Ornament corner (tintable white) ───────────────────────────────────────
c = 22
write("nq_ornament_corner", vector(
    [P(star(c,c,8,c*0.86,c*0.36), sw=1), P(star(c,c,8,c*0.5,c*0.2), sw=0.8)],
    vw=44, vh=44, w=44, h=44))

# ── School thumbnails (fixed colors) ───────────────────────────────────────
SCHOOLS = {
 "buxoro": "#3EC6C0", "fargona": "#D9A441", "samarqand": "#3D6FB4",
 "toshkent": "#C75B39", "xiva": "#7FA88B",
}
def thumb(kind, color):
    c = 28
    paths = [P(circle(c,c,c-1), fill=True, color=color+"22"),
             P(circle(c,c,c-1), sw=1, op=0.5, color=color)]
    groups = ""
    if kind == "buxoro":
        paths += [P(f"M{c} {c+9} C {c-8} {c+3}, {c-8} {c-7}, {c} {c-9} C {c+8} {c-7}, {c+8} {c+3}, {c} {c+9} Z", sw=1.4, color=color),
                  P(circle(c,c,2.2), fill=True, color=color),
                  P(f"M{c} {c-9} c -3 -2 -6 -1 -7 1", sw=1.4, color=color),
                  P(f"M{c} {c-9} c 3 -2 6 -1 7 1", sw=1.4, color=color)]
    elif kind == "fargona":
        for i in range(6):
            a = i * math.pi / 3
            paths.append(P(circle(c+5*math.cos(a), c+5*math.sin(a), 4.6), sw=1.3, color=color))
        paths.append(P(circle(c,c,2), fill=True, color=color))
    elif kind == "samarqand":
        paths += [P(star(c,c,8,10,4.4), sw=1.3, color=color), P(ring(c,c,8,4.6), sw=1.3, op=0.6, color=color)]
    elif kind == "toshkent":
        paths.append(P(rrect(c-7,c-7,14,14,0.01), sw=1.3, color=color))
        groups = (f'\n    <group android:rotation="45" android:pivotX="{c}" android:pivotY="{c}">'
                  + P(rrect(c-7,c-7,14,14,0.01), sw=1.3, color=color) + '\n    </group>')
    else:  # xiva
        paths += [P(ring(c,c,6,9), sw=1.3, color=color), P(ring(c,c,6,4.6,0), sw=1.3, color=color),
                  P(f"M{c} {c-9}V{c-4.6}", sw=1.3, color=color)]
    return vector(paths, vw=56, vh=56, w=56, h=56, groups=groups)
for kind, color in SCHOOLS.items():
    write("thumb_" + kind, thumb(kind, color))

# ── Rosette logo (fixed colors) ────────────────────────────────────────────
c = 52; fr = c - 2
paths = [P(ring(c,c,8,fr,-math.pi/8), fill=True, color="#0A3EC6C0"),
         P(ring(c,c,8,fr,-math.pi/8), sw=1, color="#2E3D5C"),
         P(ring(c,c,8,fr-5,-math.pi/8), sw=1, op=0.45, color="#D9A441")]
for i in range(8):
    a = i * math.pi / 4
    paths.append(P(circle(c+c*0.32*math.cos(a), c+c*0.32*math.sin(a), c*0.2), sw=1.4, color="#3EC6C0"))
paths += [P(circle(c,c,c*0.16), sw=1.4, color="#3EC6C0"), P(star(c,c,8,c*0.13,c*0.055), fill=True, color="#D9A441")]
write("ic_rosette", vector(paths, vw=104, vh=104, w=104, h=104))

print("Generated", len(icons), "icons + 5 thumbs + rosette + ornament corner into", OUT)
