from ovito.data     import DataCollection
from ovito.pipeline import Pipeline, StaticSource
import ovito
import os

def add_cell(w : float, h : float, x: float = 0, y: float = 0, cell_width : float = 0.1) -> None:
    cell_data = DataCollection()
    cell_dims = [[w,0,0,x],
                 [0,h,0,y],
                 [0,0,0,0]]
    
    cell = cell_data.create_cell(cell_dims, pbc=(False, False, False))
    cell.vis.line_width = cell_width
    cell_data.objects.append(cell)
    cell_pipeline = Pipeline(source=StaticSource(data=cell_data))
    cell_pipeline.add_to_scene()

def generate_visualization(w : float, h : float, L : float, ovito_file : str) -> None:
    add_cell(w, h, x=0, y=0)
    add_cell(w, L, x=w, y=(h - L)/2)

    ovito_file_split = ovito_file.rsplit('/', 1)
    if len(ovito_file_split) > 1:
        os.makedirs(ovito_file_split[0], exist_ok=True)
    ovito.scene.save(ovito_file)