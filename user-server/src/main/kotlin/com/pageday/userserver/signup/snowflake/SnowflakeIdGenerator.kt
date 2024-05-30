package com.pageday.userserver.signup.snowflake


/**
 * SnowflakeIdGenerator는 Snowflake 알고리즘을 사용하여 고유 ID를 생성하는 유틸리티 클래스입니다.
 * 이 알고리즘은 트위터에서 개발되었으며, 대규모 시스템에서 고유 ID를 생성하는 데 사용됩니다.
 *
 * Snowflake ID의 구조는 다음과 같습니다:
 * - 41비트: 타임스탬프 (커스텀 epoch 이후 밀리초 단위)
 * - 5비트: 데이터 센터 ID
 * - 5비트: 워커 ID
 * - 12비트: 시퀀스 번호
 */
class SnowflakeIdGenerator(
    private val datacenterId: Long,
    private val workerId: Long
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

    // 동일한 밀리초 내에서 고유성을 보장하기 위한 시퀀스 번호
    private var sequence = 0L
    // 마지막으로 생성된 타임스탬프를 저장하여 시계의 역방향 이동을 감지
    private var lastTimestamp = -1L

    init {
        // 데이터 센터 ID와 워커 ID의 유효성을 검사
        require(workerId in 0..MAX_WORKER_ID) { "Worker ID는 0에서 $MAX_WORKER_ID 사이여야 합니다" }
        require(datacenterId in 0..MAX_DATACENTER_ID) { "Datacenter ID는 0에서 $MAX_DATACENTER_ID 사이여야 합니다" }
    }

    /**
     * 다음 고유 ID를 생성합니다.
     * @return 고유한 64비트 ID.
     */
    @Synchronized
    fun nextId(): Long {
        var timestamp = currentTime()

        // 현재 타임스탬프가 마지막 타임스탬프보다 작으면 예외를 발생
        if (timestamp < lastTimestamp) {
            throw IllegalStateException("시계가 역방향으로 이동했습니다. ${lastTimestamp - timestamp} 밀리초 동안 ID 생성을 거부합니다")
        }

        // 현재 타임스탬프가 마지막 타임스탬프와 같으면 시퀀스를 증가
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) and SEQUENCE_MASK.toLong()
            // 시퀀스가 오버플로우되면 다음 밀리초까지 대기
            if (sequence == 0L) {
                timestamp = untilNextMillis(lastTimestamp)
            }
        } else {
            // 타임스탬프가 변경되면 시퀀스를 초기화
            sequence = 0L
        }

        // 마지막 타임스탬프를 현재 타임스탬프로 업데이트
        lastTimestamp = timestamp

        // 각 부분을 이동 및 결합하여 ID 생성
        return ((timestamp - EPOCH) shl TIMESTAMP_LEFT_SHIFT) or
                (datacenterId shl DATACENTER_ID_SHIFT) or
                (workerId shl WORKER_ID_SHIFT) or
                sequence
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
     * 시스템의 현재 시간을 밀리초 단위로 반환합니다.
     * @return 현재 시간 (밀리초 단위).
     */
    private fun currentTime(): Long = System.currentTimeMillis()
}