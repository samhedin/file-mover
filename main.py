import os
import argparse
import shutil
import pathlib

def move(mappings):
    extension_to_folder = {}
    for extension, folder in zip(mappings[0::2], mappings[1::2]):
        extension_to_folder[extension] = folder

    for f in os.listdir():
        _, extension = os.path.splitext(f)
        if extension in extension_to_folder:
            folder = extension_to_folder[extension]
            print(f"{f} -> {folder}")
            shutil.copy(f, folder)

if __name__ == '__main__':
    parser = argparse.ArgumentParser(
        prog='file-mover',
        description='Ge par av filändelse, mapp Exempel: python main.py .jpg jpegs .png pngs för att skicka alla .jpg till jpegs-mappen och .png till pngs-mappen',)
    parser.add_argument("mapping", nargs="+")
    args = parser.parse_args()
    mapping = args.mapping
    print(mapping)
    assert len(mapping) % 2 == 0, "Måste vara jämnt antal argument."
    move(mapping)
