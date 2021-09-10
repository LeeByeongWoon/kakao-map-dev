import React, { memo, useEffect, useRef, useState } from "react";

import Papa from "papaparse";

import {
    Row,
    Col,
    CardHeader,
    CardTitle,
    CardBody,
    FormGroup,
    Form,
    Button,
} from "reactstrap";
import {
    ProgressBar
} from "react-bootstrap"
import Select from "react-select";


type CsvPreviewUploadProps = {
    csvFiles: Array<File>
};

CsvPreviewUpload.defaultProps = {
    csvFiles: []
};

export function CsvPreviewUpload({ csvFiles }: CsvPreviewUploadProps) {
    const encodeType = [
        { index: 0, value: "utf-8", label: "utf-8" },
        { index: 1, value: "euc-kr", label: "euc-kr" },
        { index: 2, value: "cp949", label: "cp949" },
        { index: 3, value: "latin", label: "latin" }
    ];
    
    const inputRef = useRef(null);

    const [encode, setEncode] = useState({ index: 0, value: "utf-8", label: "utf-8" });
    

    const handleOnAddFile = (e) => {
        const { files } = e.target;

        if(files.length !== 0) {
            Papa.parse(
                files[0].slice(0, 102400),
                {
                    encoding: encode !== null ? encode.value : "utf-8",
                    complete: (results, parser) => {
                        const { data } = results;

                        const columns = data.slice(0, 1).map(
                            v => v.map(
                                (val, idx) => ({"index": idx, "type": "field", "label": val, "value": val})
                            )
                        );
                        const rows = data.slice(1, data.length)
                        
                        const table = {
                            "columns": columns,
                            "rows": rows
                        };
                
                        // setCsvData(table);
                        // setCsvFiles(files);
                    },
                    error: (error) => {
                        console.log(error);

                        // setCsvFiles([]);
                    }
                }
            );
        }
    }

    const handleOnRemoveFile = () => {
        // setCsvData(defaultCsvData);
        // setCsvFiles([]);
        inputRef.current.value = "";
    }

    return (
        <>
            <CardHeader>
                <CardTitle tag="h4">
                    1. CSV UPLOAD
                </CardTitle>
            </CardHeader>
            <CardBody>
                <Row>
                    <Col md="12">
                        <aside
                            style={{
                                display: 'flex',
                                flexDirection: 'row',
                                marginBottom: 10,
                            }}
                            >
                                <Col md="2">
                                    <Button
                                        color="primary"
                                        style={{
                                            borderRadius: 0,
                                            margin: 0,
                                            height: 40,
                                            width: '100%',
                                            paddingLeft: 0,
                                            paddingRight: 0,
                                        }}
                                        >
                                            Encoding
                                    </Button>
                                </Col>
                                <Col md="4">
                                    <Select
                                        className="react-select primary"
                                        classNamePrefix="react-select"
                                        name="mainDomainSelect"
                                        value={encode}
                                        onChange={ v => setEncode(v) }
                                        options={encodeType}
                                        placeholder="option"
                                        />
                                </Col>
                        </aside>
                    </Col>
                </Row>
                <Row>
                    <Col md="12">
                        <Form action="#" method="#">
                            <FormGroup>
                                <aside
                                    style={{
                                        display: 'flex',
                                        flexDirection: 'row',
                                        marginBottom: 10,
                                    }}
                                    >
                                        <Col md="2">
                                            <Button
                                                color="primary"
                                                style={{
                                                    borderRadius: 0,
                                                    marginTop: 5,
                                                    marginLeft: 0,
                                                    marginRight: 0,
                                                    height: 40,
                                                    width: '100%',
                                                    paddingLeft: 0,
                                                    paddingRight: 0,
                                                }}
                                                onClick={ () => inputRef.current.click() }
                                                >
                                                    <input
                                                        type="file"
                                                        accept=".csv"
                                                        id="file"
                                                        ref={inputRef}
                                                        style={{
                                                            "display": "none"
                                                        }}
                                                        onChange={ e => handleOnAddFile(e) } />
                                                    Browse file
                                            </Button>
                                        </Col>
                                        <Col md="4">
                                            <div
                                                style={{
                                                    borderWidth: 1,
                                                    borderStyle: 'solid',
                                                    borderColor: '#ccc',
                                                    height: 40,
                                                    lineHeight: 2.5,
                                                    marginTop: 5,
                                                    marginBottom: 5,
                                                    paddingLeft: 13,
                                                    paddingTop: 3,
                                                    width: '100%',
                                                }}
                                                >
                                                    {
                                                        csvFiles.length !== 0
                                                        ?
                                                            csvFiles[0].name
                                                        :
                                                            ""
                                                    }
                                            </div>
                                        </Col>
                                        <Col md="2">
                                            <Button
                                                color="danger"
                                                style={{
                                                    height: 40,
                                                    borderRadius: 0,
                                                    marginTop: 5,
                                                    marginLeft: 0,
                                                    marginRight: 0,
                                                    paddingLeft: 20,
                                                    paddingRight: 20,
                                                }}
                                                onClick={ () => handleOnRemoveFile() }
                                                >
                                                    Remove
                                            </Button>
                                        </Col>
                                </aside>
                            </FormGroup>
                        </Form>
                    </Col>
                </Row>           
            </CardBody>
        </>
    );
};