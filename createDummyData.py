import json
import random

def randstr(length):
    return ""

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
        "title": "topic " + str(id),
        "internal": bool(random.getrandbits(1))
    })

document = {
    "topic": topics,
    "motion_block": blocks
}

print(json.dumps(document))
