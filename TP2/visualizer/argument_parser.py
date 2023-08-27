from argparse import ArgumentParser

def parse_arguments() -> tuple[str, str, str, str]:
    parser = ArgumentParser()
    parser.add_argument("-s", "--static-file",  dest="static_file",      action="store", required=True)
    parser.add_argument("-d", "--dynamic-file", dest="dynamic_file",     action="store", required=True)
    parser.add_argument("--xyz",                dest="xyz_file",         action="store", required=True)
    parser.add_argument("-o", "--ovito-file",   dest="ovito_file",       action="store", required=True)
    args = parser.parse_args()
    return (args.static_file, args.dynamic_file, args.xyz_file, args.ovito_file)