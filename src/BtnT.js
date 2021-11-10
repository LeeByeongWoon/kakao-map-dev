import { DropdownMenu } from "components/main/DropDown";
import Globalstyles from "lib/GlobalStyles";
import React from "react";

function BtnT() {
  const onClick = (name) => {
    import(`res/${name}`).then((goJson) => {
      console.log(goJson);
    });
  };

  return (
    <>
      <button onClick={() => onClick("apple_2022_Rain")} style={{}}>
        불러오기
      </button>
      <button onClick={() => onClick("apple_2020_Rain")} style={{}}>
        불러오기
      </button>
    </>
  );
}

export default BtnT;
