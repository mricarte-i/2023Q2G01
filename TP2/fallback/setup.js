import { init, setN, setL, setM, setRC } from "./main";

const nInput = document.getElementById("N");
//N = nInput.value;
setN(nInput.value);
nInput.addEventListener("change", (ev) => {
  //N = ev.target.value;
  setN(ev.target.value);
  init();
})
const lInput = document.getElementById("L");
//L = lInput.value;
setL(lInput.value);
lInput.addEventListener("change", (ev) => {
  //L = ev.target.value;
  setL(ev.target.value);
  init();
})
const mInput = document.getElementById("M");
//M = mInput.value;
setM(mInput.value);
mInput.addEventListener("change", (ev) => {
  //M = ev.target.value;
  setM(ev.target.value);
  init();
})
const rcInput = document.getElementById("RC");
//RC = rcInput.value;
setRC(mInput.value);
rcInput.addEventListener("change", (ev) => {
  //RC = ev.target.value;
  setRC(ev.target.value)
  init();
})