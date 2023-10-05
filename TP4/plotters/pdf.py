from simulation_parser import SimulationInfo
import numpy as np
import math

def bin_count_by_sturges(samples : np.ndarray) -> int:
    number_of_samples = samples.size
    return math.ceil((math.log2(number_of_samples)+1))

def get_class_width(bin_edges : np.ndarray) -> float:
    class_widths = (bin_edges[1:] - bin_edges[:-1])
    return class_widths.mean()

def data_to_hist(data : np.ndarray) -> tuple[np.ndarray, np.ndarray, float]:
    bin_count         = bin_count_by_sturges(data)
    counts, bin_edges = np.histogram(data, bins=bin_count)
    class_width       = get_class_width(bin_edges)
    return (counts, bin_edges, class_width)

def probas_from_hist(counts : np.ndarray) -> np.ndarray:
    probas = counts / np.sum(counts)
    return probas

def pdf_from_probas(probas : np.ndarray, class_width : float) -> np.ndarray:
    dx = class_width
    
    pdf = probas / dx
    pdf = np.insert(pdf, 0, 0)
    pdf = np.insert(pdf, pdf.size, 0)
    return pdf

def bins_center_points(bin_edges : np.ndarray) -> np.ndarray:
    half_bins = (bin_edges[:-1] + bin_edges[1:]) / 2
    half_bins = np.insert(half_bins, 0, bin_edges[0])
    half_bins = np.insert(half_bins, half_bins.size, bin_edges[-1])
    return half_bins

def data_to_pdf(data : np.ndarray, bounds : tuple[float, float]) -> tuple[np.ndarray, np.ndarray, float]:
    counts, bin_edges, class_width = data_to_hist(data)
    
    probas = probas_from_hist(counts)
    pdf    = pdf_from_probas(probas, class_width)
    values = bins_center_points(bin_edges)

    left_bound  = bounds[0]
    right_bound = bounds[1]

    pdf = np.insert(pdf, 0, 0)
    pdf = np.insert(pdf, pdf.size, 0)

    values = np.insert(values, 0, left_bound)
    values = np.insert(values, values.size, right_bound)

    return (pdf, values, class_width)