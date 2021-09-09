import React, { memo, useState, useEffect } from 'react';

import Select from "react-select";
import {
    Card,
    CardHeader,
    CardBody,
    Row,
    Col,
    Input,
    Button,
    UncontrolledTooltip
} from "reactstrap";

const keySet = [
    { label: "field", value: "field" },
    { label: "tag", value: "tag" }
];

const CsvColumnEditor = ({ idx, val, handleOnEdit, handleOnRemove }) => {
    const { key_set, value } = val;

    const [display, setDisplay] = useState("none");
    const [editResult, setEditResult] = useState({
        column_name: value,
        key_set: {
            label: key_set,
            value: key_set
        }
    });


    return (
        <div style={{ position: "relative", display: "inline-block"}} >
            {value}
            {" "}
            <Button
                id={"Edit_" + idx}
                className="btn-icon-mini btn-neutral"
                color="info"
                title=""
                type="button"
                style={{ "padding": 0 }}
                onClick={
                    (e) => {
                        e.preventDefault();
                        if(display !== "block") {
                            setDisplay("block");
                        } else {
                            
                        }
                    }
                }
                >
                    <i className="nc-icon nc-ruler-pencil" />
            </Button>
            <UncontrolledTooltip
                delay={0}
                target={"Edit_" + idx}
                >
                    Edit
            </UncontrolledTooltip>
            <Button
                id={"Remove_" + idx}
                className="btn-icon-mini btn-neutral"
                color="danger"
                title=""
                type="button"
                style={{ "padding": 0 }}
                onClick={ () => handleOnRemove(val) }
                >
                    <i className="nc-icon nc-simple-remove" />
            </Button>
            <UncontrolledTooltip
                delay={0}
                target={"Remove_" + idx}
                >
                    Remove
            </UncontrolledTooltip>
            <Card 
                style={{
                        opacity: 1,
                        display: display,
                        position: "absolute",
                        zIndex: 99999,
                        backgroundColor: "#ffffff",
                        paddingTop: "10px"
                    }}
                >
                <CardHeader style={{ padding: 10, fontSize: "12px"}}>
                    Editor
                </CardHeader>
                <CardBody style={{ padding: 10 }}>
                    <Row>
                        <Col
                            style={{
                                fontWeight: "1",
                                fontSize: "10px"
                            }}
                            >
                                Column Name
                                <Input 
                                    style={{
                                        "display": "inline",
                                        "width": "150px"
                                    }}
                                    placeholder=""
                                    type="text"
                                    value={editResult.column_name || ""}
                                    onChange={ e => setEditResult({...editResult, column_name: e.target.value}) }
                                    />
                                <br />
                                <br />
                        </Col>
                    </Row>
                    <Row>
                        <Col
                            style={{
                                fontWeight: "1",
                                fontSize: "10px"
                            }}
                            >
                                Key Set
                                <Select
                                    className="react-select"
                                    classNamePrefix="react-select"
                                    name="mainDomainSelect"
                                    value={editResult.key_set}
                                    onChange={ v => setEditResult({ ...editResult, key_set: v }) }
                                    options={keySet}
                                    placeholder="option"
                                    />
                                <br />
                                <br />
                        </Col>
                    </Row>
                    <Row>
                        <Col
                            style={{
                                float: "right",
                                textAlign: "end"
                            }}>
                            <Button
                                size="sm"
                                color="info"
                                onClick={
                                    () => {
                                        const newData = {
                                            ...val,
                                            value: editResult.column_name,
                                            key_set: editResult.key_set.value
                                        };
                                        
                                        handleOnEdit(val, newData);
                                        setDisplay("none");
                                    }
                                }
                                >
                                    Save
                            </Button>
                            <Button
                                size="sm"
                                color="info"
                                onClick={ () => setDisplay("none") }
                                >
                                Exit
                            </Button>
                        </Col>
                    </Row>
                </CardBody>
            </Card>
        </div>
    );
}

export default memo(CsvColumnEditor);