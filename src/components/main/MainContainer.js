import React, { useState } from "react";
import styled from "styled-components";
import { ButtonContainer } from "./Btns";
import { DropdownMenu } from "./DropDown";
import { Contents } from "./Maps";
import CenterChanged from "./Maps/CenterChanged";

const Wrapper = styled.div`
  width: 100%;
  display: flex;
  flex-direction: column;
`;
function MainContainer({ map, center, container }) {
  const [fileVal, setFileVal] = useState({
    fruit: "",
    year: "",
    tag: "",
  });
  const [title, setTitle] = useState("");
  return (
    <Wrapper>
      <DropdownMenu
        fileVal={fileVal}
        setfileVal={setFileVal}
        setTitle={setTitle}
      />
      <Contents
        container={container}
        center={center}
        fileVal={fileVal}
        title={title}
      />
      <ButtonContainer map={map} fileVal={fileVal} setfileVal={setFileVal} />
    </Wrapper>
  );
}

export default MainContainer;
