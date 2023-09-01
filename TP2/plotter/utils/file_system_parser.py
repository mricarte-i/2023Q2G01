from os import walk

def directorize(directory):
    if directory[-1] != "/" : directory += "/"
    return directory

def get_files_in_directory(directory : str) -> list[str]:
    files = []
    directory = directorize(directory)
    for (dirpath, dirnames, filenames) in walk(directory):
        files.extend(filenames)
        break
    return files, directory