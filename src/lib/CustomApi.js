// /*global kakao*/
// import React, { createContext, useContext, useRef } from "react";

// const Mycontext = createContext(null);
// const containContext = createContext(null);

// export function CustomApi({ children }) {
//   const options = {
//     center: new kakao.maps.LatLng(37.54448747133563, 126.738295688373), // 지도의 중심 좌표
//     level: 12, // 확대 레벨
//   };
//   let container = useRef(null); // 지도를 표시하는 DOM 객체
//   let map = new kakao.maps.Map(container.current, options);
//   map.addOverlayMapTypeId(kakao.maps.MapTypeId.USE_DISTRICT);
//   return (
//     <Mycontext.Provider value={map}>
//       <containContext.provider value={container}>
//         {children}
//       </containContext.provider>
//     </Mycontext.Provider>
//   );
// }

// export function useMapProvider() {
//   const map = useContext(Mycontext);
//   if (!map) {
//     throw new Error("can not find mapProvider");
//   }
//   return map;
// }
// export function useContainerProvider() {
//   const container = useContext(containContext);
//   if (!container) {
//     throw new Error("can not find containerProvider");
//   }
//   return container;
// }
