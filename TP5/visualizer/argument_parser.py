from argparse import ArgumentParser

def parse_arguments() -> tuple[float, float, float, str, str, str, str, str]:
    parser = ArgumentParser()
    parser.add_argument("-w",                           dest="w",                action="store",      required=True)
    parser.add_argument("-D",                           dest="D",                action="store",      required=True)
    parser.add_argument("--dt",                         dest="dt",               action="store",      required=False)
    parser.add_argument("-s", "--static-file",          dest="static_file",      action="store",      required=True)
    parser.add_argument("-d", "--dynamic-file",         dest="dynamic_file",     action="store",      required=True)
    parser.add_argument("--xyz",                        dest="xyz_file",         action="store",      required=True)
    parser.add_argument("-o", "--ovito-file",           dest="ovito_file",       action="store",      required=True)
    parser.add_argument("-a", "--animation",            dest="animation_file",   action="store",      required=False)
    args = parser.parse_args()
    args.dt                = float(args.dt) if args.dt is not None else None
    args.w                 = float(args.w)
    args.D                 = float(args.D)
    args.static_file       = args.static_file.replace("\\", "/")
    args.dynamic_file      = args.dynamic_file.replace("\\", "/")
    args.xyz_file          = args.xyz_file.replace("\\", "/")
    args.ovito_file        = args.ovito_file.replace("\\", "/")
    args.animation_file    = args.animation_file.replace("\\", "/") if args.animation_file is not None else None
    return (args.dt, args.w, args.D, args.static_file, args.dynamic_file, args.xyz_file, args.ovito_file, args.animation_file)