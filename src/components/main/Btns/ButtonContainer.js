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
function ButtonContainer({ fileVal, setfileVal, map, setTag }) {
  const [on, off] = useState("");
  const [polygons, setPolygons] = useState([]);
  const { tag } = defaultValue;
  const tags = Object.keys(tag);
  return (
    <Wrap>
      {tags.map((tag, index) => (
        <ControllButton
          name={tag}
          fileVal={fileVal}
          setfileVal={setfileVal}
          setTag={setTag}
          map={map}
          on={on}
          off={off}
          polygons={polygons}
          setPolygons={setPolygons}
          key={index}
        />
      ))}
    </Wrap>
  );
}

export default ButtonContainer;
