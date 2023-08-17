import { setStaticIN, setDynamicIN, setNeighborData, init, getN, setN, setL, setSteps } from "./main";

function uploadNewSI(event) {
  var file = event.target.files[0];
  var filename = file.name;
  var idxDot = filename.lastIndexOf(".") + 1;
  var extFile = filename.substr(idxDot, filename.length).toLowerCase();

  if (
    extFile == "txt"
  ) {
    if (FileReader && file) {
      var fr = new FileReader();
      fr.onload = function (ev) {
        // - Static100.txt, which gives N, L, and properties on each particle
        var lines = ev.target.result.split('\n');
        var particles = [];
        for (var line = 0; line < lines.length; line++) {
          //console.log(lines[line]);
          var redLine = lines[line].split(/\s/).filter(x => !!x); //remove whitespaces
          switch (line) {
            case 0:
              //N
              var readN = redLine[0]; //there should only be one value, this one
              console.log({ N: Number(readN), readN });
              setN(Number(readN));
              break;
            case 1:
              //L
              var readL = redLine[0];
              console.log({ L: Number(readL), readL });
              setL(Number(readL));
              break;
            default:
              var readR = redLine[0];
              var readPR = redLine[1];
              //console.log({ r_i: Number(readR), pr_i: Number(readPR) });
              particles.push({ r: Number(readR), pr: Number(readPR) })
            //r_i pr_i
          }
        }

        console.log(particles);
        setStaticIN(particles);
      };

      fr.readAsText(file);
    }
  } else {
    alert("Only .txt files, please.");
  }
}
const upSI = document.getElementById("uploadSI");
upSI.addEventListener("change", (ev) => uploadNewSI(ev));


function uploadNewDI(event) {
  var file = event.target.files[0];
  var filename = file.name;
  var idxDot = filename.lastIndexOf(".") + 1;
  var extFile = filename.substr(idxDot, filename.length).toLowerCase();

  if (
    extFile == "txt"
  ) {
    if (FileReader && file) {
      var fr = new FileReader();
      fr.onload = function (ev) {
        // - Dynamic100.txt, which gives a timestamp followed by the positions and velocities of each particle at that time
        var lines = ev.target.result.split('\n');
        var N = getN();
        if (!!!N) {
          alert("please upload static input first!");
        }

        let t = undefined;
        let particles = undefined;
        for (var line = 0; line < lines.length; line++) {
          //console.log(lines[line]);
          var redLine = lines[line].split(/\s/).filter(x => !!x); //remove whitespaces
          switch (line % (N + 1)) {
            case 0:
              //time
              particles = [];
              var read = redLine[0]; //there should only be one value, this one
              t = Number(read);
              console.log({ t, read });
              break;
            default:
              //x_i y_i vx_i vy_i
              var readX = redLine[0];
              var readY = redLine[1];
              //NO VELOCITY IN THIS ONE :)
              //var readVX = redLine[2];
              //var readVY = redLine[3];
              particles.push({ x: Number(readX), y: Number(readY) })

              if ((line + 1) % (N + 1) == 0) {
                //on last particle, send data w/timestamp!
                //setParticlesAtTime(particles, t);
                //setDynamicIN(particles);
              }
          }
        }

        console.log(particles);
        //setParticlesAtTime(particles, t)
        const steps = [particles];

        let nextParticles = particles;
        for (let step = 1; step < 20; step++) {
          nextParticles = nextParticles.map((p) => {
            return {
              x: p.x + 1,
              y: p.y + 1,
            };
          });
          steps.push(nextParticles);
          //setParticlesAtTime(nextParticles, step);
        }
        setDynamicIN(steps);
        setSteps(steps.length);

        init();
      };

      fr.readAsText(file);
    }
  } else {
    alert("Only .txt files, please.");
  }
}
const upDI = document.getElementById("uploadDI");
upDI.addEventListener("change", (ev) => uploadNewDI(ev));


function uploadNewOUT(event) {
  var file = event.target.files[0];
  var filename = file.name;
  var idxDot = filename.lastIndexOf(".") + 1;
  var extFile = filename.substr(idxDot, filename.length).toLowerCase();

  if (
    extFile == "txt"
  ) {
    if (FileReader && file) {
      var fr = new FileReader();
      fr.onload = function (ev) {
        var N = getN();
        if (!!!N) {
          alert("please upload static input first!");
        }
        // - Vecinos100.txt
        // - each line is supposed to be like:
        // [0    1    2    3] - for the 0th particle, its neighbors are 1,2,3
        var lines = ev.target.result.split('\n');
        var particles = Array(N).fill(null);
        for (var line = 0; line < lines.length; line++) {
          //console.log(lines[line]);
          var redLine = lines[line].split(/\s+|\[|\]/g).filter(x => !!x).map(s => Number(s) - 1); //remove whitespaces, '[' & ']'
          var readID = redLine.shift(); //first element is the id of the particle that is "selected"
          var neighbors = redLine;
          particles[readID] = neighbors;
        }

        console.log(particles);
        setNeighborData(particles);
        init();
      }
      fr.readAsText(file);
    }
  } else {
    alert("Only .json files, please.");
  }
}

const upOUT = document.getElementById("uploadOUT");
upOUT.addEventListener("change", (ev) => uploadNewOUT(ev));
