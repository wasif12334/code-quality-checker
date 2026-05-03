async function check() {

    let code = document.getElementById("code").value;

    if (code.trim() === "") {
        document.getElementById("result").innerText = "Please enter some code.";
        return;
    }

    try {

        let res = await fetch("http://localhost:8080/api/check", {
            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify(code)
        });

        let text = await res.text();

        document.getElementById("result").innerText = text;

    } catch (error) {

        document.getElementById("result").innerText =
            "Error connecting to server.";

        console.error(error);
    }
}
