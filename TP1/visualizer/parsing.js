import { setInitCond, setNeighborData, init } from "./main";

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
        //InitCond = JSON.parse(ev.target.result);
        //TODO: read & parse two files
        // - Static100.txt, which gives N, L, and properties on each particle
        // - Dynamic100.txt, which gives a timestamp followed by the positions and velocities of each particle at that time
        setInitCond(JSON.parse(ev.target.result));
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
        //NeighborData = JSON.parse(ev.target.result);
        //TODO: read & parse data from AlgunosVecinos_100.txt
        // - each line is supposed to be like:
        // [0    1    2    3] - for the 0th particle, its neighbors are 1,2,3
        setNeighborData(JSON.parse(ev.target.result))
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