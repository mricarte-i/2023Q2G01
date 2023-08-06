var N, L, M, RC, InitCond, NeighborData = undefined;

const nInput = document.getElementById("N");
N = nInput.value;
nInput.addEventListener("change", (ev) => {
  N = ev.target.value;
  init();
})
const lInput = document.getElementById("L");
L = lInput.value;
lInput.addEventListener("change", (ev) => {
  L = ev.target.value;
  init();
})
const mInput = document.getElementById("M");
M = mInput.value;
mInput.addEventListener("change", (ev) => {
  M = ev.target.value;
  init();
})
const rcInput = document.getElementById("RC");
RC = rcInput.value;
rcInput.addEventListener("change", (ev) => {
  RC = ev.target.value;
  init();
})
const select = document.getElementById("plist");
//TODO: on selected elem, drawBase() + drawInfo()

function uploadNewIC(event) {
	var file = event.target.files[0];
	var filename = file.name;
	var idxDot = filename.lastIndexOf(".") + 1;
	var extFile = filename.substr(idxDot, filename.length).toLowerCase();

	if (
		extFile == "json"
	) {
		if (FileReader && file) {
			var fr = new FileReader();
			fr.onload = function (ev) {
        console.log(ev.target.result)
				InitCond = JSON.parse(ev.target.result);
				init();
			};
			fr.readAsText(file);
		}
	} else {
		alert("Only .json files, please.");
	}
}
const upIC = document.getElementById("uploadIC");
upIC.addEventListener("change", (ev) => uploadNewIC(ev));

function uploadNewND(event) {
	var file = event.target.files[0];
	var filename = file.name;
	var idxDot = filename.lastIndexOf(".") + 1;
	var extFile = filename.substr(idxDot, filename.length).toLowerCase();

	if (
		extFile == "json"
	) {
		if (FileReader && file) {
			var fr = new FileReader();
			fr.onload = function (ev) {
				NeighborData = JSON.parse(ev.target.result);
				init();
			};
			fr.readAsText(file);
		}
	} else {
		alert("Only .json files, please.");
	}
}

const upND = document.getElementById("uploadND");
upND.addEventListener("change", (ev) => uploadNewND(ev));


const canvas = document.getElementById("canvas");
const ctx = canvas.getContext("2d");
//canvas.width = window.innerWidth;
//canvas.height = window.innerHeight / 1.25;
canvas.width = canvas.getBoundingClientRect().width;
canvas.height = canvas.getBoundingClientRect().height;

window.addEventListener("resize", (ev) => {
  //canvas.width = window.innerWidth;
  //canvas.height = window.innerHeight / 1.25;
  canvas.width = canvas.getBoundingClientRect().width;
  canvas.height = canvas.getBoundingClientRect().height;

  drawBase()
})

const redraw = document.getElementById("redraw-btn");
redraw.addEventListener("click", (ev) => drawBase());

function removeOptions(selectElem) {
  for(let i = selectElem.options.length -1; i >= 0; i--){
    selectElem.remove(i)
  }
}

function init() {
  console.log("init!")

  removeOptions(select)
  if(!!N && N >= 1){
    select.disabled = false
    for(let i = 0; i < N; i++){
      let opt = document.createElement("option");
      opt.value = i;
      opt.innerHTML = `Particle Id:${i}`
      if(i == 0){
        opt.selected = true;
      }
      select.append(opt);
    }
  }else{
    select.disabled = true
    let opt = document.createElement("option");
    opt.value = "";
    opt.innerHTML = "--Please choose an option--";

    return;
  }

  if(!!!L || L <= 0){
    return;
  }
  if(!!!M || M <= 0){
    return;
  }
  if(!!!RC || RC <= 0){
    return;
  }
  if(!!!M || M <= 0){
    return;
  }

  drawBase()
  drawInfo()
}



function transformRange(value, range1, range2) {
  //var scale = (range2.max - range2.min) / (range1.max - range1.min);
  //return (value - range1.min) * scale + range2.min;
  return ((value - range1.min) * (range2.max - range2.min)) / (range1.max - range1.min) + range2.min;
}

function calculateSpace() {
  const minSize = Math.min(canvas.width, canvas.height);
  const sqSize = minSize - minSize/5;
  const sqX = canvas.width/2 - (sqSize/2);
  const sqY = canvas.height/2 - (sqSize/2)
  return {minSize, sqX, sqY, sqSize}
}

function drawArea(sqX, sqY, sqSize) {
  ctx.strokeStyle = 'red';
  ctx.rect(sqX, sqY, sqSize, sqSize);
  ctx.stroke();
}

function drawParticle(p, sqRX, sqRY, size, sof, color) {
  const ogRange = {min: 0, max: L*M};
  ctx.strokeStyle = color;
  ctx.beginPath();
  const newX = transformRange(p.x, ogRange, sqRX);
  const newY = transformRange(p.y, ogRange, sqRY)
  ctx.arc(newX, newY, size/150, 0, 2*Math.PI);
  if(sof == "stroke"){
    ctx.stroke();
  }else if(sof == "fill"){
    ctx.fill();
  }
}

function drawBase() {
  //clear canvas
  ctx.clearRect(0, 0, canvas.width, canvas.height);

  //TODO: draw in canvas the grid/matrix/space
  //figure out a way to calculate how to make it fit in canvas bounds
  const {minSize, sqX, sqY, sqSize} = calculateSpace();
  drawArea(sqX, sqY, sqSize)

  const sqRangeX = {min: sqX, max: sqX + sqSize};
  const sqRangeY = {min: sqY, max: sqY + sqSize};

  //check & read initial conditions
  if(!!InitCond){
    console.log("init cond draw...")
    for(let i =0; i < InitCond.length; i++){
      drawParticle(InitCond[i], sqRangeX, sqRangeY, minSize, 'stroke', 'blue');
    }
  }
  //place circles in places
  //figure out a way to draw them in the right place in the grid/matrix/space

  console.log("drawing!", {minSize, sqSize, sqX, sqY})
}

function drawInfo() {
  //check initial condifitions
  //check neighbor data
  //check selected particle

  //read necessary info

  //draw red cross on position of selected particle
  //draw green corsses on positions of neighbor particles
}

drawBase()