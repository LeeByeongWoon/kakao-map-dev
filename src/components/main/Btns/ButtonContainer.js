import React, { useState } from "react";
import styled from "styled-components";
import { ControllButton } from ".";

const Wrap = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: center;
  margin-top: 1rem;
`;
function ButtonContainer({ fileVal, setfileVal, map }) {
  const [on, off] = useState("");
  const [polygons, setPolygons] = useState([]);

  return (
    <Wrap>
      <ControllButton
        name="average"
        fileVal={fileVal}
        setfileVal={setfileVal}
        map={map}
        on={on}
        off={off}
        polygons={polygons}
        setPolygons={setPolygons}
      />
      <ControllButton
        name="rain"
        fileVal={fileVal}
        setfileVal={setfileVal}
        map={map}
        on={on}
        off={off}
        polygons={polygons}
        setPolygons={setPolygons}
      />
      <ControllButton
        name="totalAverage"
        fileVal={fileVal}
        setfileVal={setfileVal}
        map={map}
        on={on}
        off={off}
        polygons={polygons}
        setPolygons={setPolygons}
      />
      <ControllButton
        name="minTemp"
        fileVal={fileVal}
        setfileVal={setfileVal}
        map={map}
        on={on}
        off={off}
        polygons={polygons}
        setPolygons={setPolygons}
      />
    </Wrap>
  );
}

export default ButtonContainer;
