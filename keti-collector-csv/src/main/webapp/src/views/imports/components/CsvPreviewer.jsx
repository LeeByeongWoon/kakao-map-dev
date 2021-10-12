import React, { memo, useRef, useState } from "react";

import Papa from "papaparse";
import CsvColumnEditor from "./CsvColumnEditor";

import Select from "react-select";
import {
    Row,
    Col,
    Card,
    CardHeader,
    CardTitle,
    CardBody,
    Form,
    FormGroup,
    Table,
    Button,
} from "reactstrap";

const defaultCsvData = {
    "columns": 
    [
        [
            {"index": 0, "data_set": "field", "data_type": "Char", "value": "Column-1"},
            {"index": 1, "data_set": "field", "data_type": "Char", "value": "Column-2"},
            {"index": 2, "data_set": "field", "data_type": "Char", "value": "Column-3"},
            {"index": 3, "data_set": "field", "data_type": "Char", "value": "Column-4"},
            {"index": 4, "data_set": "field", "data_type": "Char", "value": "Column-5"}
        ]
    ],
    "rows":
    [
        ["Sample-1.1", "Sample-1.2", "Sample-1.3", "Sample-1.4", "Sample-1.5"],
        ["Sample-2.1", "Sample-2.2", "Sample-2.3", "Sample-2.4", "Sample-2.5"],
        ["Sample-3.1", "Sample-3.2", "Sample-3.3", "Sample-3.4", "Sample-3.5"],
        ["Sample-4.1", "Sample-4.2", "Sample-4.3", "Sample-4.4", "Sample-4.5"],
        ["Sample-5.1", "Sample-5.2", "Sample-5.3", "Sample-5.4", "Sample-5.5"]
    ]
};


const encodeType = [
    { index: 0, value: "utf-8", label: "utf-8" },
    { index: 1, value: "euc-kr", label: "euc-kr" },
    { index: 2, value: "cp949", label: "cp949" },
    { index: 3, value: "latin", label: "latin" }
];


const CsvPreviewer = ({ handleOnSetFiles, handleOnSetRules }) => {
    const inputRef = useRef(null);

    const [encode, setEncode] = useState({});

    const [csvFiles, setCsvFiles] = useState([]);
    const [csvData, setCsvData] = useState(defaultCsvData);
    const [csvCheckIn, setCsvCheckIn] = useState(false);


    const handleOnCommit = () => {
        const rules = {
            encode: encode !== null ? encode.value : "utf-8",
            columns: csvData["columns"][0]
        };

        setCsvCheckIn(true);
        handleOnSetFiles(csvFiles);
        handleOnSetRules(rules);
    }

    const handleOnRemove = (data) => {
        const { columns, rows } = csvData;
        
        const index = columns[0].indexOf(data);

        if(index !== -1) {
            const removeCsvData = {
                "columns": columns.filter(e => e.splice(index, 1)),
                "rows": rows.filter(e => e.splice(index, 1))
            };
            
            setCsvData(removeCsvData);
        }
    }

    const handleOnEdit = (oldData, newData) => {
        const { columns } = csvData;

        const index = columns[0].indexOf(oldData);

        if(index !== -1) {
            columns[0][index] = {
                ...newData
            };

            const editCsvData = {
                ...csvData,
                "columns": [
                    [
                        ...columns[0]
                    ],
                ]
            };

            setCsvData(editCsvData);
        }
    }

    const handleOnAddFile = (e) => {
        const { files } = e.target;

        if(files.length !== 0) {
            Papa.parse(
                files[0].slice(0, 102400 * 10),
                {
                    encoding: encode !== null ? encode.value : "utf-8",
                    complete: (results, parser) => {
                        const { data } = results;

                        const rows = data.slice(1, data.length);

                        const columns = data.slice(0, 1).map(
                            v => v.map(
                                (val, idx) => {
                                    let data_type = "";
                                    
                                    for (let index = 0; index < rows.length; index++) {
                                        const value = rows[index];
                                        const index_value = value[idx] !== undefined ? value[idx] : -1;
                                        const index_value_isNaN  = !isNaN(index_value);
                                            
                                        if(index_value_isNaN) {
                                            data_type = "Float";
                                        } else {
                                            data_type = "Char";
                                            break;
                                        }
                                    };

                                    return {"index": idx, "data_set": "field", "data_type": data_type, "value": val, data_format: "", data_func: []};
                                }
                            )
                        );
                        
                        const table = {
                            "columns": columns,
                            "rows": rows
                        };
                
                        setCsvData(table);
                        setCsvFiles(files);
                    },
                    error: (error) => {
                        console.log(error);

                        setCsvFiles([]);
                    }
                }
            );
        }
    }

    const handleOnRemoveFile = () => {
        setCsvData(defaultCsvData);
        setCsvFiles([]);
        inputRef.current.value = "";
    }

    return (
        <>
            <Card>
                <CardHeader>
                    <CardTitle tag="h4">1. CSV UPLOAD</CardTitle>
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
                                            value={
                                                encode.value !== undefined
                                                ?
                                                    encode
                                                :
                                                    ""
                                            }
                                            onChange={(value) => setEncode(value)}
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

                <CardHeader>
                    <CardTitle tag="h4">2. CSV COLUMNS</CardTitle>
                </CardHeader>
                <CardBody>
                    <span>
                    {
                        csvData.columns.map(
                            (v, i) => v.map(
                                (val, idx) => {
                                    const { value } = val;
                                    return (
                                        <strong key={"columns_" + i + "_" + v + "_" + idx + "_" + val}>
                                            <em>
                                                {value + "　"}
                                            </em>
                                        </strong>   
                                    )
                                }
                            )
                        )
                    }
                    </span>
                </CardBody>

                <CardHeader>
                    <CardTitle tag="h4">3. CSV TABLE</CardTitle>
                </CardHeader>
                <CardBody>
                    <Row>
                        <Col md="12">
                            <Table responsive>
                                <thead>
                                    <tr>
                                        {
                                            csvData.columns.map((v, i) => {
                                                return v.map((val, idx) => {
                                                    const { value } = val;
                                                    return (
                                                        <th 
                                                            key={"csv_table_" + i + "_" + v + "_" + idx + "_" + val}
                                                            className="text-left"
                                                            >
                                                                {
                                                                    !csvCheckIn
                                                                    ?
                                                                        <>
                                                                            <CsvColumnEditor
                                                                                idx={idx}
                                                                                val={val}
                                                                                handleOnEdit={handleOnEdit}
                                                                                handleOnRemove={handleOnRemove}
                                                                                />
                                                                        </>
                                                                    :
                                                                        <div style={{ position: "relative", display: "inline-block", width: "100%", minWidth: "96px"}}>
                                                                            {value}
                                                                        </div>
                                                                }
                                                        </th>
                                                    )
                                                })
                                            })
                                        }
                                    </tr>
                                </thead>
                                <tbody>
                                    {
                                        csvData.rows.slice(0, 7).map((v, i) => {
                                            return (
                                                <tr key={"csv_table_" + i + "_" + v}>
                                                    {
                                                        v.map((val, idx) => {
                                                            return (
                                                                <td 
                                                                    key={"csv_table_" + i + "_" + v + "_" + idx + "_" + val}
                                                                    className="text-left"
                                                                    >
                                                                        {val}
                                                                </td>
                                                            )
                                                        })
                                                    }
                                                </tr>
                                            )
                                        })
                                    }
                                </tbody>
                            </Table>
                        </Col>
                    </Row>
                    <Row>
                        <Col 
                            md="12"
                            style={{
                                "textAlign": "right",
                                "paddingTop": 0
                            }}
                            >
                                {
                                    !csvCheckIn
                                    ?
                                        <>
                                            <Button
                                                color="primary"
                                                onClick={ () => handleOnCommit() }
                                                >
                                                    save
                                            </Button>
                                        </>
                                    :
                                    ""
                                }
                        </Col>
                    </Row>        
                </CardBody>
            </Card>
        </>
    );
}

export default memo(CsvPreviewer);