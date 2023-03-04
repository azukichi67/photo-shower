import { useState, useEffect } from "react";
import React from "react";
import axios from "axios";
import defaultImage from "../assets/images/send_image.gif";

function SlideShow() {
    const imgStyle = {
        height: window.innerHeight,
        width: "auto",
    };
    const [path, setPath] = useState(defaultImage);

    useEffect(() => {
        const intervalId = setInterval(() => {
            axios
                .get("/api/images/next-image")
                .then((result) => {
                    const imageData = result.data;
                    if (imageData) {
                        setPath(`data:image/jpg;base64,${imageData}`);
                    } else {
                        setPath(defaultImage);
                    }
                })
                .catch(() => {
                    setPath(defaultImage);
                    console.log("accsess error");
                });
        }, 10000);

        return () => {
            clearInterval(intervalId);
        };
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    return (
        <div>
            <img style={imgStyle} src={`${path}`} alt="SlideShowImage" />
        </div>
    );
}

export default SlideShow;
