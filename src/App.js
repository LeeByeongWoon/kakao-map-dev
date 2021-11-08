/*global kakao*/
import { Header } from "components/base";
import { MainContainer } from "components/main";
import Globalstyles from "lib/GlobalStyles";
import { options } from "lib/style";
import React, { useEffect, useRef, useState } from "react";

function App() {
  const container = useRef(null); // 지도를 표시하는 DOM 객체
  const [map, setMap] = useState({});
  useEffect(() => {
    const map = new kakao.maps.Map(container.current, options);
    map.addOverlayMapTypeId(kakao.maps.MapTypeId.USE_DISTRICT);
    setMap(map);
  }, []);

  return (
    <>
      <Globalstyles />
      <Header map={map} />
      <MainContainer map={map} container={container} />
    </>
  );
}

export default App;
