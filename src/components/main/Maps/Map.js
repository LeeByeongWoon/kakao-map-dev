import React from "react";
import styled from "styled-components";

const MapPosition = styled.div`
  height: 30rem;
  width: 100%;
  border: 1px solid black;
`;

function Map({ container }) {
  return (
    <div>
      <MapPosition ref={container} />
    </div>
  );
}

export default Map;
