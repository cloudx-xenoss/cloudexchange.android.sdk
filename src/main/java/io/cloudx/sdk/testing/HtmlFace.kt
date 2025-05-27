package io.cloudx.sdk.testing

import io.cloudx.sdk.internal.AdNetwork

internal fun testMethod(id: String, adNetwork: AdNetwork, actualAdm: String): String {

    if (adNetwork == AdNetwork.CloudXDSP) {
        println("AdNetwork is CloudX")

        val htmlColors = listOf("red", "green", "blue", "yellow", "purple", "orange", "pink", "brown", "black")
        val randomColor = htmlColors.random()

        val html = """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Red Background</title>
    <style>
        body {
            background-color: $randomColor;
            margin: 0;
            height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            color: white;
            font-size: 24px;
        }
    </style>
</head>
<body>
    Static Empty Page
</body>
</html>
"""

        return html
    } else {
        return actualAdm
    }
}