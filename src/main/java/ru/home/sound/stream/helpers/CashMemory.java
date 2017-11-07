package ru.home.sound.stream.helpers;

import org.slf4j.Logger;
import ru.home.common.StringUtils;
import ru.home.common.logger.Slf4Logger;

import java.util.*;

public class CashMemory extends LinkedHashSet<PartData> {

    private static Logger logger = Slf4Logger.getLogger();

    private PartData firstRec;
    private PartData lastRec;

    public CashMemory() {
        super();
    }

    @Override
    public boolean add(PartData partData) {
        if (isEmpty()) {
            firstRec = partData;
        }
        lastRec = partData;
        logger.trace("{}..{}: duration = {}, size = {}",
                StringUtils.toTimeHigh(firstRec.getStartDate()),
                StringUtils.toTimeHigh(partData.getEndDate()),
                (partData.getEndDate().getTime() - firstRec.getStartDate().getTime()) / 1000.0,
                super.size() + 1
        );
        return super.add(partData);
    }

    public Date getFirstRecEndDate() {
        return firstRec != null ? firstRec.getEndDate() : null;
    }

    public void clearUnnecessaryRecords(double micSaveBeforeLengthSec) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lastRec.getStartDate());
        calendar.add(Calendar.MILLISECOND, (int) (-micSaveBeforeLengthSec * 1000));
        Date date = calendar.getTime();

        Collection<PartData> remotes = new ArrayList<>();
        firstRec = null;
        for (PartData partData : this) {
            if (partData.getEndDate().compareTo(date) > 0) {
                firstRec = partData;
                break;
            }
            remotes.add(partData);
        }
        removeAll(remotes);
        logger.trace("Чистим кэш, записей  = {}, осталось в кэше = {}", remotes.size() + size(), size());
        if (isEmpty()) {
            lastRec = null;
        }
    }

    @Override
    public boolean remove(Object o) {
        return super.remove(o);
    }

    @Override
    public void clear() {
        super.clear();
        firstRec = null;
        lastRec = null;
    }

}
