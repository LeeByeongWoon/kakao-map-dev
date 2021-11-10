import React, { useState } from "react";
import styled from "styled-components";
import { ButtonContainer } from "./Btns";
import { DropdownMenu } from "./DropDown";
import { Contents } from "./Maps";

const Wrapper = styled.div`
  width: 100%;
  display: flex;
  flex-direction: column;
`;
function MainContainer({ map, container }) {
  const [fileVal, setFileVal] = useState({
    fruit: "",
    year: "",
    tag: "",
  });
  return (
    <Wrapper>
      <DropdownMenu fileVal={fileVal} setfileVal={setFileVal} />
      <Contents container={container} />
      <ButtonContainer map={map} fileVal={fileVal} setfileVal={setFileVal} />
    </Wrapper>
  );
}

export default MainContainer;
