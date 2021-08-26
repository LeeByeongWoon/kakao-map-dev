import React, { memo, useEffect, useState } from "react";

import {
    CardHeader,
    CardTitle,
    CardBody,
    Row,
    Col,
    Table,
    Input,
    Button,
    UncontrolledTooltip
  } from "reactstrap";


const CsvViewer = (props) => {
    const { csvData, commitActive, handleOnCommitData } = props;

    const csvColumns = csvData.slice(0, 1);
    const csvRows = csvData.slice(1, csvData.length);

    const [viewColumns, setViewColumns] = useState(csvColumns);
    const [viewRows, setViewRows] = useState(csvRows.slice(0, 5));

    const [resultColumns, setResultColumns] = useState(csvColumns);
    const [resultRows, setResultRows] = useState(csvRows);
    
    const handleOnCommit = () => {
        const tempColumns = [...resultColumns];
        const tempRows = [...resultRows];

        const commitColumns = tempColumns;
        const commitRows = tempRows;

        const commitTable = commitColumns.concat(commitRows);

        handleOnCommitData(commitTable);
    }

    const handleOnEdit = (event, data) => {
        const { value } = event.target;

        const tempColumns = [...resultColumns];
        const tempRows = [...resultRows];

        const items = tempColumns[0];
        const index = items.indexOf(data);

        items[index] = {
            ...data,
            "label": value,
            "value": value
        };

        const editColumns = [items];
        const editRows = [...tempRows];

        setViewColumns(editColumns);
        setViewRows(editRows.slice(0, 5));
        setResultColumns(editColumns);
        setResultRows(editRows);
    }

    const handleOnActive = (data) => {
        const { active, value } = data

        const tempColumns = [...resultColumns];
        const tempRows = [...resultRows];

        const items = tempColumns[0];
        const index = items.indexOf(data);

        items[index] = {
            ...data,
            "active": !active ? true : false
        };

        const activeColumns = [items];
        const activeRows = [...tempRows];

        setViewColumns(activeColumns);
        setViewRows(activeRows.slice(0, 5));
        setResultColumns(activeColumns);
        setResultRows(activeRows);
    }

    const handleOnRemove = (data) => {
        const tempColumns = [...resultColumns];
        const tempRows = [...resultRows];

        const items = tempColumns[0];
        const index = items.indexOf(data);

        if(index !== -1) {
            const removeColumns = tempColumns.filter(e => e.splice(index, 1));
            const removeRows = tempRows.filter(e => e.splice(index, 1));

            setViewColumns(removeColumns);
            setViewRows(removeRows.slice(0, 5));
            setResultColumns(removeColumns);
            setResultRows(removeRows);
        }
    }

    useEffect(() => {
        setViewColumns(csvColumns);
        setViewRows(csvRows.slice(0, 5));
        setResultColumns(csvColumns);
        setResultRows(csvRows);
    }, [csvData])

    return (
        <>
            <CardHeader>
                <CardTitle tag="h4">2. CSV COLUMNS</CardTitle>
            </CardHeader>
            <CardBody>
                <span>
                {
                    viewColumns.map((v, i) => {
                        return (
                            <strong key={"csv_columns_" + i + "_" + v}>
                                {
                                    v.map((val, idx) => {
                                        const { active, value } = val;
                                        return (                
                                            <em key={"csv_columns_" + i + "_" + v + "_" + idx + "_" + val}>
                                                {value + "　"}
                                            </em>
                                        )
                                    })
                                }
                            </strong>    
                        )
                    })
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
                                        viewColumns.map((v, i) => {
                                            return v.map((val, idx) => {
                                                const { active, value } = val;
                                                
                                                return (
                                                    <th 
                                                        key={"csv_table_" + i + "_" + v + "_" + idx + "_" + val}
                                                        className="text-left"
                                                        >
                                                            {
                                                                !commitActive
                                                                ?
                                                                    value
                                                                :
                                                                    !active
                                                                    ?
                                                                        <>
                                                                            {value}
                                                                            {" "}
                                                                            <Button
                                                                                id={"Edit_" + idx}
                                                                                className="btn-icon-mini btn-neutral"
                                                                                color="info"
                                                                                title=""
                                                                                type="button"
                                                                                style={{ "padding": 0 }}
                                                                                onClick={() => handleOnActive(val)}
                                                                                >
                                                                                    <i className="nc-icon nc-ruler-pencil" />
                                                                            </Button>
                                                                            <UncontrolledTooltip
                                                                                delay={0}
                                                                                target={"Edit_" + idx}
                                                                                >
                                                                                    Edit
                                                                            </UncontrolledTooltip>
                                                                            {" "}
                                                                            <Button
                                                                                id={"Remove_" + idx}
                                                                                className="btn-icon-mini btn-neutral"
                                                                                color="danger"
                                                                                title=""
                                                                                type="button"
                                                                                style={{ "padding": 0 }}
                                                                                onClick={() => handleOnRemove(val)}
                                                                                >
                                                                                    <i className="nc-icon nc-simple-remove" />
                                                                            </Button>
                                                                            <UncontrolledTooltip
                                                                                delay={0}
                                                                                target={"Remove_" + idx}
                                                                                >
                                                                                    Remove
                                                                            </UncontrolledTooltip>
                                                                        </>
                                                                    :
                                                                        <>
                                                                            <Input 
                                                                                style={{
                                                                                    "display": "inline",
                                                                                    "width": "150px"
                                                                                }} 
                                                                                id={val}
                                                                                placeholder=""
                                                                                type="text"
                                                                                value={value}
                                                                                onChange={e => handleOnEdit(e, val)}
                                                                                onKeyPress={e => e.key !== "Enter" ? "" : handleOnActive(val)}
                                                                                />
                                                                            {" "}
                                                                            <Button
                                                                                id={"Exit_" + idx}
                                                                                className="btn-icon-mini btn-neutral"
                                                                                color="danger"
                                                                                title=""
                                                                                type="button"
                                                                                style={{ "padding": 0 }}
                                                                                onClick={() => handleOnActive(val)}
                                                                                >
                                                                                    <i className="nc-icon nc-simple-remove" />
                                                                            </Button>
                                                                        </>
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
                                    viewRows.map((v, i) => {
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
                            <Button
                                color="primary"
                                onClick={handleOnCommit}
                                >
                                    Commit
                            </Button>
                    </Col>
                </Row>        
            </CardBody>
        </>
    );
}

export default memo(CsvViewer);
