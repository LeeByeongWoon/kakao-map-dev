import react, { useState } from "React";
import styled from "styled-components";

const DropdownContainer = styled.div``;
const DropdwonContents = styled.div`
  display: ${(props) => (props.display[props.name] ? "blcok" : "none")};
`;
function DropdownMenu({ name }) {
  const [display, setDisplay] = useState({
    year: false,
    fruits: false,
  });
  const onMouseOver = (e) => {
    const [name] = e.target;
    setDisplay({
      ...display,
      [name]: true,
    });
  };
  const onMouseOut = (e) => {
    const [name] = e.target;
    setDisplay({
      ...display,
      [name]: false,
    });
  };
  return (
    <DropdownContainer>
      <DropdwonContents name={name}>
        {name == year ? "년도" : "과일"}
      </DropdwonContents>
    </DropdownContainer>
  );
}

export default DropdownMenu;
