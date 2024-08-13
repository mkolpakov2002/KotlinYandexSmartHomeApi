package ru.hse.miem.yandexsmarthomeapi

object TestConstants {

    const val SKILLS_DESCRIPTION_SOCKET = """
    {
        "request_id": "ff36a3cc-ec34-11e6-b1a0-64510650abcf",
        "payload": {
            "user_id": "user-001",
            "devices": [
                {
                    "id": "socket-001-xda",
                    "name": "розетка",
                    "description": "умная розетка xda",
                    "room": "спальня",
                    "type": "devices.types.socket",
                    "custom_data": {
                        "api_location": "rus"
                    },
                    "capabilities": [
                        {
                            "type": "devices.capabilities.on_off",
                            "retrievable": false,
                            "reportable": false,
                            "parameters": {
                                "split": false
                            }
                        }
                    ],
                    "device_info": {
                        "manufacturer": "Provider-01",
                        "model": "xda 1",
                        "hw_version": "1.2",
                        "sw_version": "5.4"
                    }
                }
            ]
        }
    }
    """

    const val SKILLS_DESCRIPTION_LAMP = """
    {
        "request_id": "ff36a3cc-ec34-11e6-b1a0-64510650abcf",
        "payload": {
            "user_id": "user-001",
            "devices": [{
                "id": "lamp-001-xdl",
                "name": "лампочка",
                "description": "умная лампочка xdl",
                "room": "спальня",
                "type": "devices.types.light",
                "custom_data": {
                    "api_location": "rus"
                },
                "capabilities": [{
                    "type": "devices.capabilities.color_setting",
                    "retrievable": true,
                    "reportable": false,
                    "parameters": {
                        "color_model": "hsv",
                        "temperature_k": {
                            "max": 6500,
                            "min": 2700
                        },
                        "color_scene": {
                            "scenes": [{
                                "id": "party"
                            }, {
                                "id": "alarm"
                            }, {
                                "id": "fantasy"
                            }, {
                                "id": "reading"
                            }]
                        }
                    }
                }],
                "device_info": {
                    "manufacturer": "Provider-01",
                    "model": "xdl 1",
                    "hw_version": "3.2",
                    "sw_version": "2.4"
                }
            }]
        }
    }
    """

    const val SKILLS_DESCRIPTION_CAMERA = """
    {
        "request_id": "ff36a3cc-ec34-11e6-b1a0-64510650abcf",
        "payload": {
            "user_id": "user-001",
            "devices": [
                {
                    "id": "cam-hd-01x",
                    "name": "моя камера",
                    "description": "умная камера",
                    "room": "спальня",
                    "type": "devices.types.camera",
                    "capabilities": [
                        {
                            "type": "devices.capabilities.video_stream",
                            "retrievable": false,
                            "reportable": false,
                            "parameters": {
                                "protocols": ["hls"]
                            }
                        }
                    ],
                    "device_info": {
                        "manufacturer": "Provider-01",
                        "model": "hd-01x",
                        "hw_version": "1.2",
                        "sw_version": "5.4"
                    }
                }
            ]
        }
    }
    """

    const val SKILLS_DESCRIPTION_AC = """
    {
        "request_id": "ff36a3cc-ec34-11e6-b1a0-64510650abcf",
        "payload": {
            "user_id": "user-001",
            "devices": [{
                "id": "ac-001-xdc",
                "name": "кондиционер",
                "description": "умный кондиционер xdc",
                "room": "спальня",
                "type": "devices.types.thermostat.ac",
                "custom_data": {
                    "api_location": "rus"
                },
                "capabilities": [{
                    "type": "devices.capabilities.mode",
                    "retrievable": true,
                    "reportable": false,
                    "parameters": {
                        "instance": "thermostat",
                        "modes": [
                            {
                                "value": "fan_only"
                            },
                            {
                                "value": "heat"
                            },
                            {
                                "value": "cool"
                            },
                            {
                                "value": "dry"
                            },
                            {
                                "value": "auto"
                            }
                        ]
                    }
                }],
                "device_info": {
                    "manufacturer": "Provider-01",
                    "model": "xdc 1",
                    "hw_version": "1.2",
                    "sw_version": "5.4"
                }
            }]
        }
    }
    """

    const val SKILLS_DESCRIPTION_LAMPS = """
    {
        "request_id": "ff36a3cc-ec34-11e6-b1a0-64510650abcf",
        "payload": {
            "user_id": "user-001",
            "devices": [{
                "id": "lamp-001-xdl",
                "name": "лампочка",
                "description": "умная лампочка xdl",
                "room": "спальня",
                "type": "devices.types.light",
                "custom_data": {
                    "api_location": "rus"
                },
                "capabilities": [{
                    "type": "devices.capabilities.range",
                    "retrievable": true,
                    "reportable": false,
                    "parameters": {
                        "instance": "brightness",
                        "random_access": true,
                        "range": {
                            "max": 100,
                            "min": 0,
                            "precision": 10
                        },
                        "unit": "unit.percent"
                    }
                }],
                "device_info": {
                    "manufacturer": "Provider-01",
                    "model": "xdl 1",
                    "hw_version": "3.2",
                    "sw_version": "2.4"
                }
            }, {
                "id": "lamp-002-xdl",
                "name": "лампочка два",
                "description": "умная лампочка xdl",
                "room": "спальня",
                "type": "devices.types.light",
                "custom_data": {
                    "api_location": "rus"
                },
                "capabilities": [{
                    "type": "devices.capabilities.range",
                    "retrievable": true,
                    "reportable": false,
                    "parameters": {
                        "instance": "brightness",
                        "random_access": true,
                        "range": {
                            "max": 100,
                            "min": 0,
                            "precision": 10
                        },
                        "unit": "unit.percent"
                    }
                }],
                "device_info": {
                    "manufacturer": "Provider-01",
                    "model": "xdl 1",
                    "hw_version": "3.2",
                    "sw_version": "2.4"
                }
            }]
        }
    }
    """

    const val SKILLS_DESCRIPTION_HUMIDIFIER = """
    {
        "request_id": "ff36a3cc-ec34-11e6-b1a0-64510650abcf",
        "payload": {
            "user_id": "user-001",
            "devices": [
                {
                    "id": "humidifier-001-xdh",
                    "name": "увлажнитель",
                    "description": "умный увлажнитель xdh",
                    "room": "спальня",
                    "type": "devices.types.humidifier",
                    "custom_data": {
                        "api_location": "rus"
                    },
                    "capabilities": [
                        {
                            "type": "devices.capabilities.toggle",
                            "retrievable": true,
                            "reportable": false,
                            "parameters": {
                                "instance": "ionization"
                            }
                        }
                    ],
                    "device_info": {
                        "manufacturer": "Provider-01",
                        "model": "xdh 1",
                        "hw_version": "1.2",
                        "sw_version": "5.4"
                    }
                }
            ]
        }
    }
    """

    const val DEVICE_ACTION_RESPONSE_SOCKET = """
    {
        "request_id": "ff36a3cc-ec34-11e6-b1a0-64510650abcf",
        "payload": {
            "devices": [
                {
                    "id": "socket-001-xda",
                    "capabilities": [
                        {
                            "type": "devices.capabilities.on_off",
                            "state": {
                                "instance": "on",
                                "action_result": {
                                    "status": "DONE"
                                }
                            }
                        }
                    ]
                }
            ]
        }
    }
    """

    const val DEVICE_ACTION_RESPONSE_LAMPS = """
    {
        "request_id": "ff36a3cc-ec34-11e6-b1a0-64510650abcf",
        "payload": {
            "devices": [{
                "id": "lamp-001-xdl",
                "capabilities": [{
                    "type": "devices.capabilities.color_setting",
                    "state": {
                        "instance": "hsv",
                        "action_result": {
                            "status": "DONE"
                        }
                    }
                }]
            },
            {
                "id": "lamp-002-xdl",
                "capabilities": [{
                    "type": "devices.capabilities.color_setting",
                    "state": {
                        "instance": "rgb",
                        "action_result": {
                            "status": "DONE"
                        }
                    }
                }]
            },
            {
                "id": "lamp-003-xdl",
                "capabilities": [{
                    "type": "devices.capabilities.color_setting",
                    "state": {
                        "instance": "temperature_k",
                        "action_result": {
                            "status": "DONE"
                        }
                    }
                }]
            },
            {
                "id": "lamp-004-xdl",
                "capabilities": [{
                    "type": "devices.capabilities.color_setting",
                    "state": {
                        "instance": "scene",
                        "action_result": {
                            "status": "DONE"
                        }
                    }
                }]
            }]
        }
    }
    """

    const val DEVICE_ACTION_RESPONSE_CAMERA = """
    {
        "request_id": "ff36a3cc-ec34-11e6-b1a0-64510650abcf",
        "payload": {
            "devices": [
                {
                    "id": "cam-hd-01x",
                    "capabilities": [
                        {
                            "type": "devices.capabilities.video_stream",
                            "state": {
                                "instance": "get_stream",
                                "value": {
                                    "stream_url": "https://host/path/to/playlist.m3u8?token=123456789abcdef",
                                    "protocol": "hls"
                                },
                                "action_result": {
                                    "status": "DONE"
                                }
                            }
                        }
                    ]
                }
            ]
        }
    }
    """

    const val DEVICE_ACTION_RESPONSE_AC = """
    {
        "request_id": "ff36a3cc-ec34-11e6-b1a0-64510650abcf",
        "payload": {
            "devices": [
                {
                    "id": "ac-001-xdc",
                    "capabilities": [
                        {
                            "type": "devices.capabilities.mode",
                            "state": {
                                "instance": "thermostat",
                                "action_result": {
                                    "status": "DONE"
                                }
                            }
                        }
                    ]
                }
            ]
        }
    }
    """

    const val DEVICE_ACTION_RESPONSE_BRIGHTNESS = """
    {
        "request_id": "ff36a3cc-ec34-11e6-b1a0-64510650abcf",
        "payload": {
            "devices": [
                {
                    "id": "lamp-001-xdl",
                    "capabilities": [
                        {
                            "type": "devices.capabilities.range",
                            "state": {
                                "instance": "brightness",
                                "action_result": {
                                    "status": "DONE"
                                }
                            }
                        }
                    ]
                },
                {
                    "id": "lamp-002-xdl",
                    "capabilities": [
                        {
                            "type": "devices.capabilities.range",
                            "state": {
                                "instance": "brightness",
                                "action_result": {
                                    "status": "DONE"
                                }
                            }
                        }
                    ]
                }
            ]
        }
    }
    """

    const val DEVICE_ACTION_RESPONSE_HUMIDIFIER = """
    {
        "request_id": "ff36a3cc-ec34-11e6-b1a0-64510650abcf",
        "payload": {
            "devices": [
                {
                    "id": "humidifier-001-xdh",
                    "capabilities": [
                        {
                            "type": "devices.capabilities.toggle",
                            "state": {
                                "instance": "ionization",
                                "action_result": {
                                    "status": "DONE"
                                }
                            }
                        }
                    ]
                }
            ]
        }
    }
    """
    const val DEVICE_QUERY_RESPONSE_LAMPS = """
    {
        "request_id": "ff36a3cc-ec34-11e6-b1a0-64510650abcf",
        "payload": {
            "devices": [
                {
                    "id": "lamp-001-xdl",
                    "capabilities": [
                        {
                            "type": "devices.capabilities.color_setting",
                            "state": {
                                "instance": "hsv",
                                "value": {
                                    "h": 255,
                                    "s": 100,
                                    "v": 50
                                }
                            }
                        }
                    ]
                },
                {
                    "id": "lamp-002-xdl",
                    "capabilities": [
                        {
                            "type": "devices.capabilities.color_setting",
                            "state": {
                                "instance": "rgb",
                                "value": 13910520
                            }
                        }
                    ]
                },
                {
                    "id": "lamp-003-xdl",
                    "capabilities": [
                        {
                            "type": "devices.capabilities.color_setting",
                            "state": {
                                "instance": "temperature_k",
                                "value": 4500
                            }
                        }
                    ]
                },
                {
                    "id": "lamp-004-xdl",
                    "capabilities": [
                        {
                            "type": "devices.capabilities.color_setting",
                            "state": {
                                "instance": "scene",
                                "value": "party"
                            }
                        }
                    ]
                }
            ]
        }
    }
    """

    const val DEVICE_QUERY_RESPONSE_CAMERA = """
    {
        "request_id": "ff36a3cc-ec34-11e6-b1a0-64510650abcf",
        "payload": {
            "devices": [
                {
                    "id": "cam-hd-01x"
                }
            ]
        }
    }
    """

    const val DEVICE_QUERY_RESPONSE_AC = """
    {
        "request_id": "ff36a3cc-ec34-11e6-b1a0-64510650abcf",
        "payload": {
            "devices": [
                {
                    "id": "ac-001-xdc",
                    "capabilities": [
                        {
                            "type": "devices.capabilities.mode",
                            "state": {
                                "instance": "thermostat",
                                "value": "cool"
                            }
                        }
                    ]
                }
            ]
        }
    }
    """

    const val DEVICE_QUERY_RESPONSE_BRIGHTNESS = """
    {
        "request_id": "ff36a3cc-ec34-11e6-b1a0-64510650abcf",
        "payload": {
            "devices": [
                {
                    "id": "lamp-001-xdl",
                    "capabilities": [
                        {
                            "type": "devices.capabilities.range",
                            "state": {
                                "instance": "brightness",
                                "value": 50
                            }
                        }
                    ]
                }
            ]
        }
    }
    """

    const val DEVICE_QUERY_RESPONSE_HUMIDIFIER = """
    {
        "request_id": "ff36a3cc-ec34-11e6-b1a0-64510650abcf",
        "payload": {
            "devices": [
                {
                    "id": "humidifier-001-xdh",
                    "capabilities": [
                        {
                            "type": "devices.capabilities.toggle",
                            "state": {
                                "instance": "ionization",
                                "value": true
                            }
                        }
                    ]
                }
            ]
        }
    }
    """

    const val DEVICE_ACTION_REQUEST_SOCKET = """
    {
        "payload": {
            "devices": [
                {
                    "id": "socket-001-xda",
                    "custom_data": {
                        "api_location": "rus"
                    },
                    "capabilities": [
                        {
                            "type": "devices.capabilities.on_off",
                            "state": {
                                "instance": "on",
                                "value": false
                            }
                        }
                    ]
                }
            ]
        }
    }
    """

    const val DEVICE_ACTION_REQUEST_LAMPS = """
    {
        "payload": {
            "devices": [{
                "id": "lamp-001-xdl",
                "custom_data": {
                    "api_location": "rus"
                },
                "capabilities": [{
                    "type": "devices.capabilities.color_setting",
                    "state": {
                        "instance": "hsv",
                        "value": {
                            "h": 125,
                            "s": 25,
                            "v": 100
                        }
                    }
                }]
            },
            {
                "id": "lamp-002-xdl",
                "custom_data": {
                    "api_location": "rus"
                },
                "capabilities": [{
                    "type": "devices.capabilities.color_setting",
                    "state": {
                        "instance": "rgb",
                        "value": 14210514
                    }
                }]
            },
            {
                "id": "lamp-003-xdl",
                "custom_data": {
                    "api_location": "rus"
                },
                "capabilities": [{
                    "type": "devices.capabilities.color_setting",
                    "state": {
                        "instance": "temperature_k",
                        "value": 5100
                    }
                }]
            },
            {
                "id": "lamp-004-xdl",
                "capabilities": [{
                    "type": "devices.capabilities.color_setting",
                    "state": {
                        "instance": "scene",
                        "value": "party"
                    }
                }]
            }]
        }
    }
    """

    const val DEVICE_ACTION_REQUEST_CAMERA = """
    {
        "payload": {
            "devices": [
                {
                    "id": "cam-hd-01x",
                    "custom_data": {
                        "api_location": "rus"
                    },
                    "capabilities": [
                        {
                            "type": "devices.capabilities.video_stream",
                            "state": {
                                "instance": "get_stream",
                                "value": {
                                    "protocols": [
                                        "hls"
                                    ]
                                }
                            }
                        }
                    ]
                }
            ]
        }
    }
    """

    const val DEVICE_ACTION_REQUEST_AC = """
    {
        "payload": {
            "devices": [{
                "id": "ac-001-xdc",
                "custom_data": {
                    "api_location": "rus"
                },
                "capabilities": [{
                    "type": "devices.capabilities.mode",
                    "state": {
                        "instance": "thermostat",
                        "value": "heat"
                    }
                }]
            }]
        }
    }
    """

    const val DEVICE_ACTION_REQUEST_BRIGHTNESS = """
    {
        "payload": {
            "devices": [{
                "id": "lamp-001-xdl",
                "custom_data": {
                    "api_location": "rus"
                },
                "capabilities": [{
                    "type": "devices.capabilities.range",
                    "state": {
                        "instance": "brightness",
                        "value": 50
                    }
                }]
            },
            {
                "id": "lamp-002-xdl",
                "custom_data": {
                    "api_location": "rus"
                },
                "capabilities": [{
                    "type": "devices.capabilities.range",
                    "state": {
                        "instance": "brightness",
                        "relative": true,
                        "value": 10
                    }
                }]
            }]
        }
    }
    """

    const val DEVICE_ACTION_REQUEST_HUMIDIFIER = """
    {
        "payload": {
            "devices": [{
                "id": "humidifier-001-xdh",
                "custom_data": {
                    "api_location": "rus"
                },
                "capabilities": [{
                    "type": "devices.capabilities.toggle",
                    "state": {
                        "instance": "ionization",
                        "value": false
                    }
                }]
            }]
        }
    }
    """
}