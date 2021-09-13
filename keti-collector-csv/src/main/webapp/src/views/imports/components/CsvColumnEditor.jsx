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


const CsvColumnEditor = ({ idx, val, handleOnEdit, handleOnRemove }) => {
    const dataSet = [
        { label: "field", value: "field" },
        { label: "tag", value: "tag" }
    ];
    
    const dataType = [
        { label: "Float", value: "Float" },
        { label: "Char", value: "Char" }
    ];

    const { data_set, data_type, value } = val;

    const [display, setDisplay] = useState("none");
    const [editResult, setEditResult] = useState({
        column_name: value,
        data_set: {
            label: data_set,
            value: data_set
        },
        data_type: {
            label: data_type,
            value: data_type
        }
    });


    return (
        <div style={{ position: "relative", display: "inline-block", width: "100%", minWidth: "96px"}} >
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
                                Data Set
                                <Select
                                    className="react-select"
                                    classNamePrefix="react-select"
                                    name="dataSetSelect"
                                    value={editResult.data_set}
                                    onChange={ v => setEditResult({ ...editResult, data_set: v }) }
                                    options={dataSet}
                                    placeholder="option"
                                    />
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
                                Data Type
                                <Select
                                    className="react-select"
                                    classNamePrefix="react-select"
                                    name="dataTypeSelect"
                                    value={editResult.data_type}
                                    onChange={ v => setEditResult({ ...editResult, data_type: v }) }
                                    options={dataType}
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
                                            data_set: editResult.data_set.value,
                                            data_type: editResult.data_type.value
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