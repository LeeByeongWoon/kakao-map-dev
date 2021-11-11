import { defaultValue } from "lib/style";
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
  const { tag } = defaultValue;
  const tags = Object.keys(tag);
  return (
    <Wrap>
      <ControllButton
        name={tags[0]}
        fileVal={fileVal}
        setfileVal={setfileVal}
        map={map}
        on={on}
        off={off}
        polygons={polygons}
        setPolygons={setPolygons}
      />
      <ControllButton
        name={tags[1]}
        fileVal={fileVal}
        setfileVal={setfileVal}
        map={map}
        on={on}
        off={off}
        polygons={polygons}
        setPolygons={setPolygons}
      />
      <ControllButton
        name={tags[2]}
        fileVal={fileVal}
        setfileVal={setfileVal}
        map={map}
        on={on}
        off={off}
        polygons={polygons}
        setPolygons={setPolygons}
      />
      <ControllButton
        name={tags[3]}
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
