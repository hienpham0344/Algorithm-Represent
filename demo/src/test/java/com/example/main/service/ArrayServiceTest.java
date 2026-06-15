package com.example.main.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArrayServiceTest {

    @Test
    void resetRestoresSampleArray() {
        ArrayService service = new ArrayService();
        service.deleteEnd();

        ArrayService.Result result = service.reset();

        assertTrue(result.success());
        assertEquals(List.of(12, 45, 8, 5, 23, 19, 56), service.toList());
    }

    @Test
    void insertEndAndInsertAtPlaceValuesCorrectly() {
        ArrayService service = new ArrayService();

        ArrayService.Result endResult = service.insertEnd(99);
        ArrayService.Result indexResult = service.insertAt(2, 77);

        assertTrue(endResult.success());
        assertEquals(7, endResult.index());
        assertTrue(indexResult.success());
        assertEquals(2, indexResult.index());
        assertEquals(List.of(12, 45, 77, 8, 5, 23, 19, 56, 99), service.toList());
    }

    @Test
    void insertAtAcceptsBothBoundaryIndexes() {
        ArrayService service = new ArrayService();

        assertTrue(service.insertAt(0, 1).success());
        assertTrue(service.insertAt(service.size(), 2).success());
        assertEquals(1, service.toList().get(0));
        assertEquals(2, service.toList().get(service.size() - 1));
    }

    @Test
    void insertRejectsInvalidIndexesWithoutChangingArray() {
        ArrayService service = new ArrayService();
        List<Integer> before = service.toList();

        assertFalse(service.insertAt(-1, 99).success());
        assertFalse(service.insertAt(service.size() + 1, 99).success());
        assertEquals(before, service.toList());
    }

    @Test
    void arrayRejectsInsertionsAfterTwentyValues() {
        ArrayService service = new ArrayService();
        while (service.size() < ArrayService.MAX_CAPACITY) {
            assertTrue(service.insertEnd(service.size()).success());
        }

        ArrayService.Result endResult = service.insertEnd(100);
        ArrayService.Result indexResult = service.insertAt(0, 200);

        assertTrue(service.isFull());
        assertFalse(endResult.success());
        assertFalse(indexResult.success());
        assertEquals(ArrayService.MAX_CAPACITY, service.size());
        assertTrue(endResult.message().contains("20"));
    }

    @Test
    void deleteEndAndDeleteAtRemoveExpectedValues() {
        ArrayService service = new ArrayService();

        ArrayService.Result endResult = service.deleteEnd();
        ArrayService.Result indexResult = service.deleteAt(1);

        assertTrue(endResult.success());
        assertEquals(56, endResult.value());
        assertTrue(indexResult.success());
        assertEquals(45, indexResult.value());
        assertEquals(List.of(12, 8, 5, 23, 19), service.toList());
    }

    @Test
    void deleteRejectsInvalidIndexesWithoutChangingArray() {
        ArrayService service = new ArrayService();
        List<Integer> before = service.toList();

        assertFalse(service.deleteAt(-1).success());
        assertFalse(service.deleteAt(service.size()).success());
        assertEquals(before, service.toList());
    }

    @Test
    void updateChangesOnlyRequestedIndex() {
        ArrayService service = new ArrayService();

        ArrayService.Result result = service.updateAt(3, 88);

        assertTrue(result.success());
        assertEquals(3, result.index());
        assertEquals(88, service.toList().get(3));
        assertEquals(7, service.size());
    }

    @Test
    void updateRejectsInvalidIndexes() {
        ArrayService service = new ArrayService();
        List<Integer> before = service.toList();

        assertFalse(service.updateAt(-1, 88).success());
        assertFalse(service.updateAt(service.size(), 88).success());
        assertEquals(before, service.toList());
    }

    @Test
    void searchReturnsFirstMatchAndReportsMissingValue() {
        ArrayService service = new ArrayService();
        service.insertAt(0, 23);

        ArrayService.Result found = service.search(23);
        ArrayService.Result missing = service.search(999);

        assertTrue(found.success());
        assertEquals(0, found.index());
        assertFalse(missing.success());
        assertNull(missing.index());
    }

    @Test
    void returnedListCannotMutateServiceState() {
        ArrayService service = new ArrayService();
        List<Integer> copy = service.toList();

        copy.clear();

        assertEquals(7, service.size());
        assertFalse(service.isEmpty());
    }

    @Test
    void operationsReportEmptyArrayFailures() {
        ArrayService service = new ArrayService();
        while (!service.isEmpty()) {
            service.deleteEnd();
        }

        assertFalse(service.deleteEnd().success());
        assertFalse(service.deleteAt(0).success());
        assertFalse(service.updateAt(0, 1).success());
        assertFalse(service.search(1).success());
    }
}
