from argparse import ArgumentParser

def parse_order_over_iterations_arguments() -> tuple[str, str, str, str, str, float]:
    parser = ArgumentParser()
    parser.add_argument("-s", "--static-files",       dest="static_files",       action="store", required=True)
    parser.add_argument("-d", "--dynamic-files",      dest="dynamic_files",      action="store", required=True)
    parser.add_argument("-p", "--polarization-files", dest="polarization_files", action="store", required=True)
    parser.add_argument("-c", "--colors-file",        dest="colors_file",        action="store", required=True)
    parser.add_argument("-o", "--out-image-file",     dest="out_image_file",     action="store", required=True)
    parser.add_argument("--max-iter",                 dest="max_iter",           action="store", required=True)
    parser.add_argument("--min-iter",                 dest="min_iter",           action="store", required=True)
    args = parser.parse_args()
    args.static_files       = args.static_files.replace("\\", "/")
    args.dynamic_files      = args.dynamic_files.replace("\\", "/")
    args.polarization_files = args.polarization_files.replace("\\", "/")
    args.colors_file        = args.colors_file.replace("\\", "/")
    args.out_image_file     = args.out_image_file.replace("\\", "/")
    args.max_iter           = float(args.max_iter)
    args.min_iter           = float(args.min_iter)
    return (args.static_files, args.dynamic_files, args.polarization_files, args.colors_file, args.out_image_file, args.min_iter, args.max_iter)

def parse_order_over_noise_arguments() -> str:
    parser = ArgumentParser()
    parser.add_argument("-d", "--directory",  dest="directory",  action="store", required=True)
    parser.add_argument("-i", "--va-instant", dest="va_instant", action="store", required=True)
    args = parser.parse_args()
    args.directory = args.directory.replace("\\", "/")
    args.va_instant = int(args.va_instant)
    return args.directory, args.va_instant