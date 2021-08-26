import React, { memo, useEffect, useRef, useState } from "react";

import { ReadableWebToNodeStream } from 'readable-web-to-node-stream';
import { CSVReader } from "react-papaparse";
import Papa from "papaparse";

import {
    CardHeader,
    CardTitle,
    CardBody,
    FormGroup,
    Form,
    Row,
    Col,
    Button
} from "reactstrap";

const enc = new TextDecoder("utf-8");


const UploadFile = (files, callback) => {
    const file = files[0];

    const chunk_size = 1048576 * 10;
    let offset = 0;
    
    const fr = new window.FileReader();

    fr.onload = () => {
        const progress_rate = (offset / file.size*100).toFixed(0) + "%";
        const chunk_data = new window.Uint8Array(fr.result);
        
        callback(progress_rate, chunk_data);
        offset += chunk_size;
        seek();
    };

    fr.onerror = function() {
        console.log("에러");
    };

    seek();

    function seek () {
        if (offset >= file.size) {
            return;
        }

        const slice = file.slice(offset, offset + chunk_size);
        fr.readAsArrayBuffer(slice);
    }
}


const CsvUpload = (props) => {
    const { defaultCsvData, handleOnUploadCsvData, handleOnUpdateCsvData } = props;

    const [csvFiles, setCsvFiles] = useState([]);
    const [csvText, setCsvText] = useState("");

    const inputFileRef = useRef(null);

    const hadleOnFileSelector = (e) => {
        const { value, files } = e.target;

        if(value !== undefined) {
            setCsvFiles(files);
            handleOnCsvFile(files);
        }
    }

    const handleOnCsvFile = (files) => {
        const file = files[0];
        
        const data = Papa.parse(
            file,
            {
                "delimiter": "",	// auto-detect
                "newline": "",	// auto-detect
                "quoteChar": '"',
                "escapeChar": '"',
                "header": false,
                "transformHeader": undefined,
                "dynamicTyping": false,
                "preview": 0,
                "encoding": "",
                "worker": false,
                "comments": false,
                "step": undefined,
                "complete": undefined,
                "error": undefined,
                "download": false,
                "downloadRequestHeaders": undefined,
                "downloadRequestBody": undefined,
                "skipEmptyLines": false,
                "chunk": (results, parser) => {
                    console.log(results);
                },
                "chunkSize": Papa.LocalChunkSize,
                "fastMode": true,
                "beforeFirstChunk": undefined,
                "withCredentials": undefined,
                "transform": undefined,
                "delimitersToGuess": [',', '\t', '|', ';', Papa.RECORD_SEP, Papa.UNIT_SEP]
            }
        );

        // UploadFile(
        //     files,
        //     (progress, data) => {
        //         console.log(progress, enc.decode(data));
        //     }
        // );
    }

    return (
        <>
            <CardHeader>
                <CardTitle tag="h4">1. CSV UPLOAD</CardTitle>
            </CardHeader>
            <CardBody>
                <Row>
                    <Col md="6">
                        <Form action="#" method="#">
                            <FormGroup>
                            <input 
                                type="file"
                                id="file"
                                style={{"display": "none"}}
                                ref={inputFileRef} 
                                onChange={e => hadleOnFileSelector(e)}
                                />
                            <Button 
                                className="btn-round" 
                                color="info" 
                                type="button" 
                                onClick={
                                    () => {
                                        inputFileRef.current.click();
                                    }
                                }
                                >
                                Upload
                            </Button>
                            <Button 
                                className="btn-round" 
                                color="info" 
                                type="button" 
                                onClick={
                                    () => {
                                        
                                    }
                                }
                                >
                                Test
                            </Button>
                            </FormGroup>
                        </Form>
                    </Col>
                </Row>           
            </CardBody>
        </>
    );
}

export default memo(CsvUpload);
