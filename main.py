import os
import argparse
import shutil
import pathlib

def move(mappings):
    m = {}
    print("moving: ")
    for extension, folder in zip(mappings[0::2], mappings[1::2]):
        print(f"*{extension}->{folder}")
        m[extension] = folder

    for f in os.listdir():
        _, extension = os.path.splitext(f)
        if extension in m:
            shutil.copy(f, m[extension])

if __name__ == '__main__':
    parser = argparse.ArgumentParser(
        prog='file-mover',
        description='Ge par av filändelse, mapp Exempel: .jpg, jpegs, .png, pngs för att skicka alla .jpg till jpegs-mappen och .png till pngs-mappen',)
    parser.add_argument("mapping", nargs="+")
    args = parser.parse_args()
    mapping = args.mapping
    print(mapping)
    assert len(mapping) % 2 == 0, "Måste vara jämnt antal argument."
    move(mapping)
