package de.eldoria.shepard.commandmodules.kudos.commands;

import de.eldoria.shepard.basemodules.commanddispatching.util.ArgumentParser;
import de.eldoria.shepard.commandmodules.Command;
import de.eldoria.shepard.commandmodules.argument.Parameter;
import de.eldoria.shepard.commandmodules.argument.SubCommand;
import de.eldoria.shepard.commandmodules.command.CommandUsage;
import de.eldoria.shepard.commandmodules.command.Executable;
import de.eldoria.shepard.commandmodules.kudos.data.KudoData;
import de.eldoria.shepard.commandmodules.kudos.util.KudoLotteryEvaluator;
import de.eldoria.shepard.commandmodules.util.CommandCategory;
import de.eldoria.shepard.messagehandler.ErrorType;
import de.eldoria.shepard.messagehandler.MessageSender;
import de.eldoria.shepard.minigameutil.ChannelEvaluator;
import de.eldoria.shepard.modulebuilder.requirements.ReqDataSource;
import de.eldoria.shepard.modulebuilder.requirements.ReqInit;
import de.eldoria.shepard.modulebuilder.requirements.ReqShardManager;
import de.eldoria.shepard.wrapper.EventContext;
import de.eldoria.shepard.wrapper.EventWrapper;
import net.dv8tion.jda.api.sharding.ShardManager;

import javax.sql.DataSource;
import java.util.OptionalInt;

/**
 * Command to start a new KudoLottery.
 * A started lottery will be handled by {@link KudoLotteryEvaluator}
 */
@CommandUsage(EventContext.GUILD)
public class KudoLottery extends Command implements Executable, ReqShardManager, ReqDataSource, ReqInit {
    private ShardManager shardManager;
    private ChannelEvaluator<KudoLotteryEvaluator> evaluator;
    private KudoData kudoData;

    /**
     * Creates a new kudo lottery command object.
     */
    public KudoLottery(ChannelEvaluator<KudoLotteryEvaluator> evaluator) {
        super("kudoLottery",
                new String[]{"lottery", "kl"},
                "command.kudoLottery.description",
                SubCommand.builder("kudoLottery")
                        .addSubcommand("command.kudoLottery.command.maxBet",
                                Parameter.createInput("command.general.argument.amount", "command.general.argumentDescription.amount", false))
                        .build(),
                "command.kudoLottery.command.default",
                CommandCategory.FUN);
        this.evaluator = evaluator;
    }

    @Override
    public void execute(String label, String[] args, EventWrapper wrapper) {
        boolean success = kudoData.tryTakePoints(wrapper.getGuild().get(),
                wrapper.getAuthor(), 1, wrapper);

        int maxBet = 100;

        if (args.length > 0) {
            OptionalInt amount = ArgumentParser.parseInt(args[0]);
            if (amount.isEmpty()) {
                MessageSender.sendSimpleError(ErrorType.NOT_A_NUMBER, wrapper);
                return;
            }
            maxBet = Math.min(Math.max(amount.getAsInt(), 1), 500);
        }

        if (!success) {
            MessageSender.sendSimpleError(ErrorType.NOT_ENOUGH_KUDOS, wrapper);
            return;
        }

        evaluator.scheduleEvaluation(180,
                new KudoLotteryEvaluator(kudoData, evaluator, shardManager, wrapper.getActor(),
                        maxBet, wrapper.getGuild().get(), wrapper.getTextChannel().get()));
    }

    @Override
    public void addShardManager(ShardManager shardManager) {
        this.shardManager = shardManager;
    }

    @Override
    public void init() {
    }

    @Override
    public void addDataSource(DataSource source) {
        kudoData = new KudoData(source);
    }
}
