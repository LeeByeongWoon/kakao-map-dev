import React, { useState } from "react";
import styled from "styled-components";
import DropdownBtn from "./DropdownBtn";
import { defaultValue } from "lib/style";

const DropdownContainer = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: center;
  margin-bottom: 0.5rem;
`;
const DataSet = {
  year: "",
  fruit: "",
  tag: "defualt",
};

function DropdownMenu() {
  const { fruit, year, tag } = defaultValue;
  const [fileName, setFilename] = useState(DataSet);

  return (
    <DropdownContainer>
      <DropdownBtn data={year} name="year"></DropdownBtn>
      <DropdownBtn data={fruit} name="fruit"></DropdownBtn>
    </DropdownContainer>
  );
}

export default DropdownMenu;
