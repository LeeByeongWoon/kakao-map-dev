import { createAction, handleActions } from "redux-actions";

const ACTIVE = "indicator/ACTIVE";
const INACTIVE = "indicator/INACTIVE";


export const active = createAction(ACTIVE);
export const inactive = createAction(INACTIVE);


const initialState = {
    indicator: false
};


export default handleActions(
    {
        [ACTIVE]: (state, action) => ({ indicator: false }),
        [INACTIVE]: (state, action) => ({ indicator: true })
    },
    initialState
);