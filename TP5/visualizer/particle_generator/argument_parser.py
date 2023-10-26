from argparse import ArgumentParser

def parse_arguments() -> tuple[str, str]:
    parser = ArgumentParser()
    parser.add_argument("-s", "--static-file",          dest="static_file",      action="store",      required=True)
    parser.add_argument("-d", "--dynamic-file",         dest="dynamic_file",     action="store",      required=True)
    args = parser.parse_args()
    args.static_file       = args.static_file.replace("\\", "/")
    args.dynamic_file      = args.dynamic_file.replace("\\", "/")
    return (args.static_file, args.dynamic_file)