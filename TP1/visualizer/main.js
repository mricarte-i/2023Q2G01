import './style.css'
import './setup'
import './parsing'

/**
 *  SIM VARIABLES & DATA
 */
var N, L, M, RC, InitCond, NeighborData, Selected = undefined;

export function setN(val) { N = val; }
export function setL(val) { L = val; }
export function setM(val) { M = val; }
export function setRC(val) { RC = val; }

export function setInitCond(val) { InitCond = val; }
export function setNeighborData(val) { NeighborData = val; }

/**
 *  HTML SETUP
 */

const select = document.getElementById("plist");
select.addEventListener("change", (ev) => {
  Selected = ev.target.value;
  drawAll();
})


const canvas = document.getElementById("canvas");
const ctx = canvas.getContext("2d");
canvas.width = canvas.getBoundingClientRect().width;
canvas.height = canvas.getBoundingClientRect().height;

window.addEventListener("resize", (ev) => {
  canvas.width = canvas.getBoundingClientRect().width;
  canvas.height = canvas.getBoundingClientRect().height;

  drawAll()
})

const redraw = document.getElementById("redraw-btn");
redraw.addEventListener("click", (ev) => drawBase());

/**
 *  HELPER FUNCTIONS
 *
 */
function removeOptions(selectElem) {
  for (let i = selectElem.options.length - 1; i >= 0; i--) {
    selectElem.remove(i)
  }
}

function transformRange(value, range1, range2) {
  return ((value - range1.min) * (range2.max - range2.min)) / (range1.max - range1.min) + range2.min;
}

function calculateSpace() {
  const minSize = Math.min(canvas.width, canvas.height);
  const sqSize = minSize - minSize / 5;
  const sqX = canvas.width / 2 - (sqSize / 2);
  const sqY = canvas.height / 2 - (sqSize / 2);

  const sqRangeX = { min: sqX, max: sqX + sqSize };
  const sqRangeY = { min: sqY, max: sqY + sqSize };

  return { minSize, sqX, sqY, sqSize, sqRangeX, sqRangeY }
}

/**
 *  On change to parameters or uploaded files
 *  - remake particle selector
 *  - check for invalid values
 *  - redraw canvas
 */
export function init() {
  ctx.save();
  console.log("init!")

  removeOptions(select)
  if (!!N && N >= 1) {
    select.disabled = false
    for (let i = 0; i < N; i++) {
      let opt = document.createElement("option");
      opt.value = i;
      opt.innerHTML = `Particle Id:${i}`
      if (i == 0) {
        opt.selected = true;
        Selected = "0";
      }
      select.append(opt);
    }
  } else {
    Selected = undefined;
    select.disabled = true
    let opt = document.createElement("option");
    opt.value = "";
    opt.innerHTML = "--Please choose an option--";

    return;
  }

  if (!!!L || L <= 0) {
    return;
  }
  if (!!!M || M <= 0) {
    return;
  }
  if (!!!RC || RC <= 0) {
    return;
  }
  if (!!!M || M <= 0) {
    return;
  }

  drawAll();
}

function drawAll() {
  drawBase();
  drawInfo();
}


function drawGrid() {
  ctx.restore();

  console.log('grid')
  const ogRange = { min: 0, max: L * M };
  const { sqX, sqY, sqSize, sqRangeX, sqRangeY } = calculateSpace();
  for (let i = 0; i < L * M; i++) {
    ctx.beginPath();
    ctx.setLineDash([5, 15]);
    ctx.moveTo(transformRange(i, ogRange, sqRangeX), sqY);
    ctx.lineTo(transformRange(i, ogRange, sqRangeX), sqSize + sqY);
    ctx.strokeStyle = 'red';
    //ctx.lineWidth = '5';
    ctx.stroke();
  }

  for (let i = 0; i < L * M; i++) {
    ctx.beginPath();
    ctx.setLineDash([5, 15]);
    ctx.moveTo(sqX, transformRange(i, ogRange, sqRangeY));
    ctx.lineTo(sqSize + sqX, transformRange(i, ogRange, sqRangeY));
    ctx.strokeStyle = 'red';
    //ctx.lineWidth = '5';
    ctx.stroke();
  }
  ctx.setLineDash([]);
}

function drawArea(sqX, sqY, sqSize) {
  ctx.restore();

  ctx.beginPath();
  ctx.strokeStyle = 'red';
  ctx.rect(sqX, sqY, sqSize, sqSize);
  ctx.stroke();

  drawGrid(sqX, sqY)
}

function drawParticle(p, sqRX, sqRY, size, sof, color) {
  ctx.restore();

  const ogRange = { min: 0, max: L * M };
  ctx.beginPath();
  ctx.strokeStyle = sof == "stroke" ? color : 'white';
  ctx.fillStyle = sof == "fill" ? color : 'white';
  const newX = transformRange(p.x, ogRange, sqRX);
  const newY = transformRange(p.y, ogRange, sqRY);
  ctx.arc(newX, newY, (size / (L * M)) * 0.1, 0, 2 * Math.PI);
  if (sof == "stroke") {
    ctx.stroke();
  } else if (sof == "fill") {
    ctx.fill();
  }

  //write ids
  ctx.beginPath();
  ctx.fillStyle = 'black'
  ctx.textBaseline = 'middle';
  ctx.textAlign = 'center';
  ctx.font = `20px sans-serif`
  ctx.fillText(`${p.id}`, newX, newY)

}

function drawInteractionRadius(p, sqRX, sqRY, size, color) {
  ctx.restore();

  const ogRange = { min: 0, max: L * M };
  ctx.beginPath();
  ctx.strokeStyle = color;
  ctx.fillStyle = 'transparent';

  const newX = transformRange(p.x, ogRange, sqRX);
  const newY = transformRange(p.y, ogRange, sqRY);

  //show interaction radius
  ctx.arc(newX, newY, ((size / (L * M)) * RC), 0, 2 * Math.PI);
  ctx.stroke();

  //trying to figure out how to scale to pixels...
  /*
  console.log(
    { px: size, full: L * M },
    { px: ((size / (L * M)) * RC), full: RC },
    { px: (size / (L * M)), full: 1 }
  )
  */
}

function drawBase() {
  //clear canvas
  ctx.clearRect(0, 0, canvas.width, canvas.height);

  const { minSize, sqX, sqY, sqSize, sqRangeX, sqRangeY } = calculateSpace();
  drawArea(sqX, sqY, sqSize)

  //check & read initial conditions
  if (!!InitCond) {
    console.log("init cond draw...")
    for (let i = 0; i < InitCond.length; i++) {
      drawParticle(InitCond[i], sqRangeX, sqRangeY, sqSize, 'stroke', 'blue');
    }
  }
  console.log("drawing!", { minSize, sqSize, sqX, sqY })
}

function drawInfo() {
  //check initial condifitions
  if (!!!InitCond) return;
  //check neighbor data
  if (!!!NeighborData) return;
  //check selected particle
  if (!!!Selected) return;

  console.log("draw info on particles...")
  const { sqSize, sqRangeX, sqRangeY } = calculateSpace();
  const selectedIdx = Number(Selected);
  //draw red cross on position of selected particle
  drawParticle(InitCond[selectedIdx], sqRangeX, sqRangeY, sqSize, 'fill', 'red');
  drawInteractionRadius(InitCond[selectedIdx], sqRangeX, sqRangeY, sqSize, 'magenta')

  console.log("SELECTED", InitCond[selectedIdx], RC)

  //read necessary info
  const selectedNeighbors = NeighborData[selectedIdx];

  if (!!!selectedNeighbors) return;
  //draw green corsses on positions of neighbor particles
  for (let i = 0; i < selectedNeighbors.length; i++) {
    const nId = selectedNeighbors[i].id;
    drawParticle(InitCond[nId], sqRangeX, sqRangeY, sqSize, 'fill', 'green');

    console.log(
      "IS NEAR!",
      InitCond[nId],
      {
        distance: Math.hypot(InitCond[selectedIdx].x - InitCond[nId].x, InitCond[selectedIdx].y - InitCond[nId].y)
      }
    );
  }
}

drawBase()
