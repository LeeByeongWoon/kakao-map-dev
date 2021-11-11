/*global kakao*/
import { Header } from "components/base";
import { MainContainer } from "components/main";
import CenterChanged from "components/main/Maps/CenterChanged";
import Globalstyles from "lib/GlobalStyles";
import { options } from "lib/style";
import React, { useEffect, useRef, useState } from "react";

function App() {
  const container = useRef(null); // 지도를 표시하는 DOM 객체
  const [map, setMap] = useState({});
  const [center, setCenter] = useState({
    Lat: "",
    Lng: "",
  });
  useEffect(() => {
    const map = new kakao.maps.Map(container.current, options);
    // map.addOverlayMapTypeId(kakao.maps.MapTypeId.USE_DISTRICT);
    setMap(map);

    kakao.maps.event.addListener(map, "center_changed", function () {
      const latlng = map.getCenter();
      setCenter({
        ...center,
        Lat: latlng.getLat(),
        Lng: latlng.getLng(),
      });
    });
  }, []);

  return (
    <>
      <Globalstyles />
      <Header map={map} />
      <MainContainer map={map} center={center} container={container} />
    </>
  );
}

export default App;
