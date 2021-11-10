import React from "react";
import styled from "styled-components";
import DropdownBtn from "./DropdownBtn";
import { defaultValue } from "lib/style";

const DropdownContainer = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: center;
  margin-bottom: 0.5rem;
`;

function DropdownMenu({ fileVal, setfileVal }) {
  const { fruit, year } = defaultValue;

  return (
    <DropdownContainer>
      <DropdownBtn
        data={year}
        name="year"
        fileVal={fileVal}
        setfileVal={setfileVal}></DropdownBtn>
      <DropdownBtn
        data={fruit}
        name="fruit"
        fileVal={fileVal}
        setfileVal={setfileVal}></DropdownBtn>
    </DropdownContainer>
  );
}

export default DropdownMenu;
