/*global kakao*/
import oc from "open-color";

export const colorSchema = {
  1: oc.red[5], //red[deps]
  2: oc.grape[5], // grape[deps]
  3: oc.indigo[5], // Indigo[deps]
  4: oc.cyan[5], // Cyan[deps]
  5: oc.yellow[5], // yellow[deps]
};

export const theme = (weight) => {
  return oc.indigo[weight];
};

// map style
export const options = {
  center: new kakao.maps.LatLng(37.54448747133563, 126.738295688373), // 지도의 중심 좌표
  level: 12, // 확대 레벨
};

const yearList = () => {
  let tmp = [];
  for (let i = 1980; i <= 2060; i += 10) {
    tmp.push(i);
  }
  return tmp;
};

const yy = yearList();
export const defaultValue = {
  fruit: ["apple", "peach", "mango", "dragonfruits", "pear"],
  year: [...yy],
  tag: ["default", "average", "rain", "TotalAverage", "minTemperature"],
};
