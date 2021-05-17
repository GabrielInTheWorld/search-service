import json
import random

def randstr(length):
    result = ""
    for i in range(length):
        result += chr(Math.randint(32, 126))
    return result

topics = []
for id in range(1, 10):
    topics.append({
        "id": id,
        "title": "topic " + str(id),
        "text": randstr(1024)
    })

blocks = []
for id in range(1, 10):
    blocks.append({
        "id": id,
        "title": "block " + str(id),
        "internal": bool(random.getrandbits(1))
    })

document = {
    "topic": topics,
    "motion_block": blocks
}

print(json.dumps(document))
