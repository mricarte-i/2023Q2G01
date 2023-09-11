from argparse import ArgumentParser

def parse_arguments() -> tuple[float, str, str, str]:
    parser = ArgumentParser()
    parser.add_argument("-L", "--snd-container-height", dest="L",              action="store", required=True)
    parser.add_argument("-s", "--static-file",          dest="static_file",    action="store", required=True)
    parser.add_argument("-d", "--dynamic-file",         dest="dynamic_file",   action="store", required=True)
    #parser.add_argument("--xyz",                        dest="xyz_file",       action="store", required=True)
    parser.add_argument("-o", "--ovito-file",           dest="ovito_file",     action="store", required=True)
    #parser.add_argument("-a", "--animation",            dest="animation_file", action="store", required=True)
    args = parser.parse_args()
    args.L                 = float(args.L)
    args.static_file       = args.static_file.replace("\\", "/")
    args.dynamic_file      = args.dynamic_file.replace("\\", "/")
    #args.xyz_file          = args.xyz_file.replace("\\", "/")
    args.ovito_file        = args.ovito_file.replace("\\", "/")
    #args.animation_file    = args.animation_file.replace("\\", "/")
    return (args.L, args.static_file, args.dynamic_file, args.ovito_file)