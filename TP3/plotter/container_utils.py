def get_perimeter(L : float) -> float:
    side = 0.09

    left_container_perimeter  = side * 3 + (side - L)
    right_container_perimeter = side * 2 + L

    total_perimeter = left_container_perimeter + right_container_perimeter

    return total_perimeter

def __get_left_container_area__() -> float:
    side = 0.09
    area = side * side
    return area

def __get_right_container_area__(L : float) -> float:
    side = 0.09
    area = side * L
    return area

def get_area(L : float) -> float:
    left_container_area  = __get_left_container_area__()
    right_container_area = __get_right_container_area__(L)

    total_area = left_container_area + right_container_area

    return total_area