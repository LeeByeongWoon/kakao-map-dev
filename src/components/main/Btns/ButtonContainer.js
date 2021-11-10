import React from "react";
import styled from "styled-components";
import { ControllButton } from ".";

const Wrap = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: center;
  margin-top: 1rem;
`;
function ButtonContainer({ fileVal, setfileVal, map }) {
  return (
    <Wrap>
      <ControllButton
        name="average"
        fileVal={fileVal}
        setfileVal={setfileVal}
        map={map}
      />
      <ControllButton name="rain" fileVal={fileVal} setfileVal={setfileVal} />
      <ControllButton
        name="totalAverage"
        fileVal={fileVal}
        setfileVal={setfileVal}
        map={map}
      />
      <ControllButton
        name="minTemp"
        fileVal={fileVal}
        setfileVal={setfileVal}
        map={map}
      />
    </Wrap>
  );
}

export default ButtonContainer;
