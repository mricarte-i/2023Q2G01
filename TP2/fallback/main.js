import './style.css'
import './setup'
import './parsing'

/**
 *  SIM VARIABLES & DATA
 *  @N number of particles
 *  @L size of area (space)
 *  @M number of cells
 *  @RC interaction radius
 */
var N, L, M, RC, StaticIN, DynamicIN, NeighborData, Selected = undefined;
var Step = 0, Steps = undefined;
var PlayAnimation = false;
var MillisPerFrame = 125;

export function setN(val) { N = val; }
export function setL(val) { L = val; }
export function setM(val) { M = val; }
export function setRC(val) { RC = val; }

export function getN() { return N; }
export function getL() { return L; }
export function getM() { return M; }
export function getRC() { return RC; }

export function setStaticIN(val) { StaticIN = val; }
export function setDynamicIN(val) { DynamicIN = val; }
export function setNeighborData(val) { NeighborData = val; }
export function setSteps(val) { Steps = val; Step = 0; }

function getCurrentDynamic() {
  return DynamicIN[Step];
}

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

let zoomMult = 1;
const player = {
  x: 0,
  y: 0
};
const cam = {
  x: 0,
  y: 0
}


window.addEventListener("resize", (ev) => {
  canvas.width = canvas.getBoundingClientRect().width;
  canvas.height = canvas.getBoundingClientRect().height;

  drawAll()
})

const redraw = document.getElementById("redraw-btn");
redraw.addEventListener("click", (ev) => drawAll());

const timeStepSlider = document.getElementById("timeStepSlider");
timeStepSlider.addEventListener("change", (ev) => {
  const val = ev.target.value;
  if (Steps != undefined) {
    Step = transformRange(val, { min: 0, max: 100 }, { min: 0, max: Steps - 1 });
  }
  drawAll();
});

const prevStepBtn = document.getElementById("prevStep-btn");
prevStepBtn.addEventListener("click", (ev) => {
  Step = mod(Step - 1, Steps);
  console.log(Step);
  timeStepSlider.value = transformRange(Step, { min: 0, max: Steps - 1 }, { min: 0, max: 100 });
  drawAll()
});

const nextStepBtn = document.getElementById("nextStep-btn");
nextStepBtn.addEventListener("click", (ev) => {
  Step = mod(Step + 1, Steps);
  console.log(Step);
  timeStepSlider.value = transformRange(Step, { min: 0, max: Steps - 1 }, { min: 0, max: 100 });
  drawAll();
});

let animationIntervalId = undefined;
timeStepSlider.disabled = false;

const playBtn = document.getElementById("autoPlay-btn");
playBtn.addEventListener("click", (ev) => {
  PlayAnimation = !PlayAnimation;
  if (!PlayAnimation && animationIntervalId != undefined) {
    clearInterval(animationIntervalId);
    timeStepSlider.disabled = false;
    animationIntervalId = undefined;
  } else {
    timeStepSlider.disabled = true;
    animationIntervalId = setInterval(() => {
      Step = mod(Step + 1, Steps);
      console.log(Step);
      timeStepSlider.value = transformRange(Step, { min: 0, max: Steps - 1 }, { min: 0, max: 100 });
      drawAll();
    }, MillisPerFrame);
  }
})

/**
 *  HELPER FUNCTIONS
 *
 */
function mod(n, m) {
  return ((n % m) + m) % m;
}

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
  const sqSize = minSize - minSize / 10;
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
  //console.log("init!")

  removeOptions(select)
  if (!!N && N >= 1) {
    select.disabled = false
    for (let i = 0; i < N; i++) {
      let opt = document.createElement("option");
      opt.value = i;
      opt.innerHTML = `Particle Id:${i + 1}`
      if (i == 0 && (!!!Selected || Number(Selected) >= N)) {
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

  player.x = canvas.width / (2 * zoomMult);
  player.y = canvas.height / (2 * zoomMult);

  drawAll();
}

function runSerial(tasks) {
  return new Promise((resolve) => {
    const promised = [];
    Promise
      .allSettled(tasks)
      .then((results) => {
        results.forEach((result) => {
          promised.push(result.status);
        });

        return resolve(promised);
      });

  })

}

let drawCallState = 0;

function drawAll() {
  if (drawCallState == 0) {
    drawCallState = 1;
    runSerial([drawBase(), drawInfo()]).then((val) => {
      //console.log(val);
      drawCallState = 0;
    });
  }
}


function drawGrid() {
  ctx.save();

  //console.log('grid')
  const ogRange = { min: 0, max: L };
  const { sqX, sqY, sqSize, sqRangeX, sqRangeY } = calculateSpace();
  for (let i = 0; i < L; i++) {
    ctx.beginPath();
    //ctx.setLineDash([5, 15]);
    ctx.moveTo(transformRange(i, ogRange, sqRangeX), sqY);
    ctx.lineTo(transformRange(i, ogRange, sqRangeX), sqSize + sqY);
    ctx.strokeStyle = 'rgba(245, 0, 0, 0.24)';
    ctx.lineWidth = '0.5px';
    ctx.stroke();
  }

  for (let i = 0; i < L; i++) {
    ctx.beginPath();
    //ctx.setLineDash([5, 15]);
    ctx.moveTo(sqX, transformRange(i, ogRange, sqRangeY));
    ctx.lineTo(sqSize + sqX, transformRange(i, ogRange, sqRangeY));
    ctx.strokeStyle = 'rgba(245, 0, 0, 0.24)';
    ctx.lineWidth = '0.5px';
    ctx.stroke();
  }
  ctx.setLineDash([]);

  ctx.restore();
}

function drawArea(sqX, sqY, sqSize) {
  ctx.save();

  ctx.beginPath();
  ctx.strokeStyle = 'red';
  ctx.lineWidth = '1px'
  ctx.rect(sqX, sqY, sqSize, sqSize);
  ctx.stroke();

  ctx.restore();

  drawGrid(sqX, sqY)
}

function drawParticle(id, sqRX, sqRY, size, sof, color) {
  ctx.save();
  const currentDynamic = getCurrentDynamic();

  const ogRange = { min: 0, max: L };
  ctx.beginPath();
  ctx.strokeStyle = sof == "stroke" ? color : 'white';
  ctx.fillStyle = sof == "fill" ? color : 'white';
  const newX = transformRange(currentDynamic[id].x, ogRange, sqRX);
  const newY = transformRange(currentDynamic[id].y, ogRange, sqRY);
  ctx.arc(newX, newY, (size / L) * StaticIN[id].r, 0, 2 * Math.PI);
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
  ctx.fillText(`${id + 1}`, newX, newY);

  ctx.restore();

}

function drawInteractionRadius(id, sqRX, sqRY, size, color) {
  ctx.save();
  const currentDynamic = getCurrentDynamic();

  const ogRange = { min: 0, max: L };
  ctx.beginPath();
  ctx.strokeStyle = color;
  ctx.fillStyle = 'transparent';

  const newX = transformRange(currentDynamic[id].x, ogRange, sqRX);
  const newY = transformRange(currentDynamic[id].y, ogRange, sqRY);

  //show interaction radius
  ctx.arc(newX, newY, ((size / L) * (StaticIN[id].r + StaticIN[id].pr)), 0, 2 * Math.PI);
  ctx.stroke();

  ctx.restore();

  //trying to figure out how to scale to pixels...
  /*
  console.log(
    { px: size, full: L * M },
    { px: ((size / (L * M)) * RC), full: RC },
    { px: (size / (L * M)), full: 1 }
  )
  */
}

function setCtx() {
  ctx.scale(zoomMult, zoomMult);
  cam.x = player.x - canvas.width / (2 * zoomMult);
  cam.y = player.y - canvas.height / (2 * zoomMult);
  ctx.translate(-cam.x, -cam.y);

  //console.log({ zoomMult, cam, player });
}

const drawBase = () => new Promise(
  (resolve) => {
    //clear canvas
    ctx.setTransform(1, 0, 0, 1, 0, 0);
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    setCtx();

    const { minSize, sqX, sqY, sqSize, sqRangeX, sqRangeY } = calculateSpace();
    drawArea(sqX, sqY, sqSize)

    //check & read initial conditions
    if (!!StaticIN && !!DynamicIN) {
      //console.log("init cond draw...")
      for (let i = 0; i < N; i++) {
        drawParticle(i, sqRangeX, sqRangeY, sqSize, 'stroke', 'blue');
      }
    }
    //console.log("drawing!", { minSize, sqSize, sqX, sqY })
    return resolve();
  }
);

const drawInfo = () => new Promise(
  (resolve, reject) => {
    //check selected particle
    if (!!!Selected) return reject();
    //check initial condifitions
    if (!!!StaticIN || !!!DynamicIN) return reject();
    //console.log("draw info on particles...")
    const { sqSize, sqRangeX, sqRangeY } = calculateSpace();
    const selectedIdx = Number(Selected);
    //draw red cross on position of selected particle
    drawParticle(selectedIdx, sqRangeX, sqRangeY, sqSize, 'fill', 'red');
    drawInteractionRadius(selectedIdx, sqRangeX, sqRangeY, sqSize, 'magenta');

    //check neighbor data
    if (!!!NeighborData) return reject();

    const currentDynamic = getCurrentDynamic();

    console.log("SELECTED", selectedIdx + 1, StaticIN[selectedIdx], currentDynamic[selectedIdx], RC)

    //read necessary info
    const selectedNeighbors = NeighborData[selectedIdx];

    if (!!!selectedNeighbors) return reject();
    //draw green corsses on positions of neighbor particles
    for (let i = 0; i < selectedNeighbors.length; i++) {
      const nId = selectedNeighbors[i];
      drawParticle(nId, sqRangeX, sqRangeY, sqSize, 'fill', 'green');

      console.log(
        "IS NEAR!",
        nId + 1,
        StaticIN[nId], currentDynamic[nId],
        {
          distance: Math.hypot(currentDynamic[selectedIdx].x - currentDynamic[nId].x, currentDynamic[selectedIdx].y - currentDynamic[nId].y) - StaticIN[selectedIdx].r - StaticIN[nId].r
        }
      );
    }
    return resolve();
  }
)


function clamp(value, min, max) {
  if (value < min) return min;
  else if (value > max) return max;
  return value;
}


var listened = false;
window.addEventListener("keyup", (ev) => {
  listened = false;
  if (ev.key == "i") {
    zoomMult += 0.1;
    listened = true;
  }
  if (ev.key == "o") {
    zoomMult -= 0.1;
    listened = true;
  }
  if (ev.key == "ArrowLeft") {
    player.x -= L / 10;  //Left key
    listened = true;
  }
  if (ev.key == "ArrowUp") {
    player.y -= L / 10;  //Up key
    listened = true;
  }
  if (ev.key == "ArrowRight") {
    player.x += L / 10; //Right key
    listened = true;
  }
  if (ev.key == "ArrowDown") {
    player.y += L / 10; //Down key
    listened = true;
  }


  clamp(zoomMult, 0.5, 2);
  clamp(player.x, -10, L + 10);
  clamp(player.y, -10, L + 10);
  if (listened) {
    drawAll();
  }
})



drawAll();