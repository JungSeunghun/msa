package com.pageday.userserver.common.snowflake

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.slf4j.LoggerFactory

/**
 * SnowflakeIdGenerator는 Snowflake 알고리즘을 사용하여 고유 ID를 생성하는 유틸리티 클래스입니다.
 * 이 알고리즘은 트위터에서 개발되었으며, 대규모 시스템에서 고유 ID를 생성하는 데 사용됩니다.
 *
 * Snowflake ID의 구조는 다음과 같습니다:
 * - 41비트: 타임스탬프 (커스텀 epoch 이후 밀리초 단위)
 * - 5비트: 데이터 센터 ID
 * - 5비트: 워커 ID
 * - 12비트: 시퀀스 번호
 *
 * @property datacenterId 데이터 센터 ID (0-31)
 * @property workerId 워커 ID (0-31)
 */
class SnowflakeIdGenerator(
    private val datacenterId: Long,
    private val workerId: Long,
    private val timeProvider: () -> Long = System::currentTimeMillis
) {
    companion object {
        private const val EPOCH = 1288834974657L // 커스텀 epoch (2010-11-04 01:42:54 UTC)
        private const val WORKER_ID_BITS = 5
        private const val DATACENTER_ID_BITS = 5
        private const val MAX_WORKER_ID = (1 shl WORKER_ID_BITS) - 1
        private const val MAX_DATACENTER_ID = (1 shl DATACENTER_ID_BITS) - 1
        private const val SEQUENCE_BITS = 12
        private const val WORKER_ID_SHIFT = SEQUENCE_BITS
        private const val DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS
        private const val TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS
        private const val SEQUENCE_MASK = (1 shl SEQUENCE_BITS) - 1
    }

    private var sequence = 0L
    private var lastTimestamp = -1L
    private val lock = Mutex()
    private val logger = LoggerFactory.getLogger(SnowflakeIdGenerator::class.java)

    init {
        require(workerId in 0..MAX_WORKER_ID) { "Worker ID는 0에서 $MAX_WORKER_ID 사이여야 합니다" }
        require(datacenterId in 0..MAX_DATACENTER_ID) { "Datacenter ID는 0에서 $MAX_DATACENTER_ID 사이여야 합니다" }
    }

    /**
     * 다음 고유 ID를 생성합니다.
     * @return 고유한 64비트 ID.
     */
    suspend fun nextId(): Long {
        return lock.withLock {
            var timestamp = currentTime()

            if (timestamp < lastTimestamp) {
                handleClockBackward(timestamp)
                timestamp = currentTime()
            }

            if (lastTimestamp == timestamp) {
                sequence = (sequence + 1) and SEQUENCE_MASK.toLong()
                if (sequence == 0L) {
                    timestamp = untilNextMillis(lastTimestamp)
                }
            } else {
                sequence = 0L
            }

            lastTimestamp = timestamp

            ((timestamp - EPOCH) shl TIMESTAMP_LEFT_SHIFT) or
                    (datacenterId shl DATACENTER_ID_SHIFT) or
                    (workerId shl WORKER_ID_SHIFT) or
                    sequence
        }
    }

    /**
     * 시계가 역방향으로 이동했을 때의 처리 로직.
     * @param timestamp 현재 타임스탬프.
     */
    private suspend fun handleClockBackward(timestamp: Long) {
        val offset = lastTimestamp - timestamp
        if (offset > 0) {
            logger.warn("시계가 역방향으로 이동했습니다. ${offset} 밀리초 동안 대기 후 재시도합니다.")
            delay(offset)
        }
    }

    /**
     * 타임스탬프가 고유해질 때까지 대기합니다.
     * @param lastTimestamp 마지막으로 사용된 타임스탬프.
     * @return 다음 타임스탬프.
     */
    private fun untilNextMillis(lastTimestamp: Long): Long {
        var timestamp = currentTime()
        while (timestamp <= lastTimestamp) {
            timestamp = currentTime()
        }
        return timestamp
    }

    /**
     * 현재 시간을 밀리초 단위로 반환합니다.
     * @return 현재 시간 (밀리초 단위).
     */
    private fun currentTime(): Long = timeProvider()
}
