package com.demo.service.impl;

import com.demo.service.ChatRecord;
import com.demo.service.ChatService;
import org.junit.Test;

import java.util.Set;
import static org.junit.Assert.*;

public class ChatServiceImplTest {
    @Test
    public void addTest() {
        final ChatService unit = new ChatServiceMemoryImpl();
        unit.addChat(new ChatRecordImpl(11111L, Set.of(1L, 2L, 3L, 4L, 5L)));
        unit.addChat(new ChatRecordImpl(22222L, Set.of(1011L, 1012L, 1013L, 1014L, 1015L)));

        assertNotNull(unit.getChat(11111L));
        assertNotNull(unit.getChat(22222L));
    }

    @Test
    public void addDuplicateTest() {
        final ChatService unit = new ChatServiceMemoryImpl();
        unit.addChat(new ChatRecordImpl(11111L, Set.of(1L, 2L, 3L, 4L, 5L)));

        try {
            unit.addChat(new ChatRecordImpl(11111L, Set.of(1011L, 1012L, 1013L, 1014L, 1015L)));
            fail("Excpected exception");
        } catch (IllegalStateException e) {
            final ChatRecord record = unit.getChat(11111L);
            assertNotNull(record);
            assertEquals(Set.of(1L, 2L, 3L, 4L, 5L), record.getParticipantIds());

        } catch (Exception e) {
            fail("Caught unexpecgted exception " + e);
        }

    }
}
