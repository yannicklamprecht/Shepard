package de.eldoria.shepard.commandmodules.kudos;

import de.eldoria.shepard.commandmodules.SharedResources;
import de.eldoria.shepard.commandmodules.kudos.commands.KudoLottery;
import de.eldoria.shepard.commandmodules.kudos.commands.Kudos;
import de.eldoria.shepard.commandmodules.kudos.listener.KudoLotteryListener;
import de.eldoria.shepard.commandmodules.kudos.scheduler.KudoCounter;
import de.eldoria.shepard.commandmodules.kudos.util.KudoLotteryEvaluator;
import de.eldoria.shepard.minigameutil.ChannelEvaluator;
import de.eldoria.shepard.modulebuilder.ModuleBuilder;

public class KudoModule implements ModuleBuilder {
    @Override
    public void buildModule(SharedResources resources) {
        ChannelEvaluator<KudoLotteryEvaluator> evaluator = new ChannelEvaluator<>(5);
        addAndInit(new KudoLotteryListener(evaluator), resources);
        addAndInit(new Kudos(), resources);
        addAndInit(new KudoLottery(evaluator), resources);
        addAndInit(new KudoCounter(), resources);

        //addAndInit(new KudoGamble(), resources);
    }
}
