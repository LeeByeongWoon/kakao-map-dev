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
const temp = yy.map((year) => {
  return {
    FileName: year,
    Print: year + "년",
  };
});

export const defaultValue = {
  fruit: [
    {
      FileName: "apple",
      Print: "사과",
    },
    {
      FileName: "peach",
      Print: "복숭아",
    },
    {
      FileName: "mango",
      Print: "망고",
    },
    {
      FileName: "dragonfruits",
      Print: "용과",
    },
    {
      FileName: "pear",
      Print: "배",
    },
  ],
  year: [...temp],
  tag: {
    default: "종합",
    rain: "연간강수량",
    totalaverage: "연평균 기온",
    average: "1월평균기온",
  },
};
