/*
 * Copyright 2016 OPEN TONE Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.co.opentone.bsol.framework.web.view.util;

import org.junit.Test;

import jp.co.opentone.bsol.framework.core.message.Message;
import junit.framework.Assert;

/**
 * MessageQueueクラスのテストケース.
 * @author opentone
 */
public class MessageQueueTest {

    @Test
    public void testAdd() {
        MessageQueue queue = new MessageQueue();
        // CHECKSTYLE:OFF
        Message[] messageList = new Message[] {
            new Message("01", ""),
            new Message("05", ""),
            new Message("02", ""),
            new Message("10", ""),
            new Message("09", ""),
        };
        // CHECKSTYLE:ON
        for (Message message : messageList) {
            queue.add(message);
        }
        Assert.assertEquals(messageList.length, queue.size());
    }

    @Test
    public void testPoll() {
        MessageQueue queue = new MessageQueue();
        // CHECKSTYLE:OFF
        Message[] messageList = new Message[] {
            new Message("01", ""),
            new Message("05", ""),
            new Message("02", ""),
            new Message("10", ""),
            new Message("09", ""),
        };
        // CHECKSTYLE:ON
        queue.add(messageList);
        for (int i = 0; i < messageList.length; i++) {
            Assert.assertEquals(messageList[i].getMessageCode(), queue.poll().getMessageCode());
        }
        Assert.assertNull(queue.poll());
    }

    @Test
    public void testClear() {
        MessageQueue queue = new MessageQueue();
        // CHECKSTYLE:OFF
        Message[] messageList = new Message[] {
            new Message("01", ""),
            new Message("05", ""),
            new Message("02", ""),
            new Message("10", ""),
            new Message("09", ""),
        };
        // CHECKSTYLE:ON
        queue.add(messageList);
        Assert.assertEquals(messageList.length, queue.size());
        queue.clear();
        Assert.assertEquals(0, queue.size());
    }

    @Test
    public void testPollArray() {
        MessageQueue queue = new MessageQueue();
        // CHECKSTYLE:OFF
        Message[] messageList = new Message[] {
            new Message("01", ""),
            new Message("05", ""),
            new Message("02", ""),
            new Message("10", ""),
            new Message("09", ""),
        };
        // CHECKSTYLE:ON
        queue.add(messageList);
        Message[] arrayList = queue.pollArray();
        for (int i = 0; i < messageList.length; i++) {
            Assert.assertEquals(messageList[i].getMessageCode(), arrayList[i].getMessageCode());
        }
        Assert.assertEquals(0, queue.size());
        Assert.assertNull(queue.poll());
    }
}
